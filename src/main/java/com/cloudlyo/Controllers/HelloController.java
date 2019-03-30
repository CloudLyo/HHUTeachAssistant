package com.cloudlyo.Controllers;

import com.cloudlyo.Constant.Apis;
import com.cloudlyo.Constant.AppInfo;
import com.cloudlyo.DataEntity.*;
import com.cloudlyo.JsonBean.Code2Session;
import com.cloudlyo.JsonBean.LoginBean;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;

    @RequestMapping("/login")
    public String login(@RequestParam(value = "code", defaultValue = "null") String code){
        System.out.println(code);
        Response response=null;
        LoginBean loginBean = new LoginBean();
        String res = "";
        OkHttpClient client  = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Apis.code2Session
                        + "appid="+ AppInfo.AppID
                        + "&secret="+AppInfo.AppSecret
                        + "&js_code="+code
                        + "&grant_type=authorization_code")
                .build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            loginBean.result=-1;
        }
        if (response==null) loginBean.result=-1;
        String openid="";
        try {
            String body = response.body().string();
            Code2Session session = new Gson().fromJson(body,Code2Session.class);
            openid = session.openid;
            //System.out.println(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (openid==null||openid.equals("")) loginBean.result = -1;
        else loginBean.openId = openid;

        System.out.println(openid);

       List<UserEntity> users = userRepository.findByOpenId(openid);
       if (users==null||users.size()==0){
           userRepository.save(new UserEntity(openid,"null"));
       }else{
           loginBean.job = users.get(0).getCurrentJob();
       }

        Gson gson = new Gson();
        res = gson.toJson(loginBean,LoginBean.class);
        System.out.println(res);
        return res;
    }

    @RequestMapping("/choosejob")
    public String login(@RequestParam(value = "openId", defaultValue = "null") String openId,@RequestParam(value = "choice", defaultValue = "null") String choice){
        System.out.println(openId+" "+choice);

        UserEntity user = userRepository.findByOpenId(openId).get(0);
        user.setCurrentJob(choice);
        userRepository.save(user);

        if (choice.equals("teacher")){
            List<TeacherEntity> teacher = teacherRepository.findByOpenId(openId);
            if (teacher==null||teacher.size()<=0){
                teacherRepository.save(new TeacherEntity(openId,"","",""));
            }
        }else{
            List<StudentEntity> student = studentRepository.findByOpenId(openId);
            if (student==null||student.size()<=0){
                studentRepository.save(new StudentEntity(openId,"","",""));
            }
        }
        return "";
    }

}