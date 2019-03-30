package com.cloudlyo.Controllers;

import com.cloudlyo.DataEntity.*;
import com.cloudlyo.JsonBean.ClassListBean;
import com.cloudlyo.JsonBean.LocationBean;
import com.cloudlyo.JsonBean.UserInfoBean;
import com.cloudlyo.Utiles.PositionUtil;
import com.google.gson.Gson;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentRepository studentRepository ;
    @Autowired
    ClassRepository classRepository;


    @RequestMapping("/addclass")
    public String addClass(@RequestParam(value = "openId",defaultValue = "null")String openId,
                           @RequestParam(value = "classId",defaultValue = "0")String Id){
        long classId = Long.parseLong(Id);
        StudentEntity stu = studentRepository.findByOpenId(openId).get(0);
        String classes = stu.getClasses();
        List<ClassEntity> clas = classRepository.findById(classId);
        if (clas==null||clas.size()==0) return "课程不存在";
        clas.get(0).setStudents(clas.get(0).getStudents()+"_"+stu.getOpenId());
        classRepository.save(clas.get(0));
        stu.setClasses(stu.getClasses()+"_"+clas.get(0).getId());
        studentRepository.save(stu);
        return "添加成功";
    }

    @RequestMapping("/getclass")
    public String getClass(@RequestParam(value = "openId",defaultValue = "null")String openId){
        ArrayList<ClassListBean> clas = new ArrayList<>();
        StudentEntity student = studentRepository.findByOpenId(openId).get(0);
        String[] clastr = student.getClasses().split("_");
        for (String claid : clastr){
            if (claid.equals("")) continue;
            long id = Long.parseLong(claid);
            ClassEntity cla = classRepository.findById(id).get(0);
            String name = cla.getName();
            clas.add(new ClassListBean(name,id));
        }
        Gson gson = new Gson();
        String res = gson.toJson(clas);
        System.out.println(res);
        return res;
    }
    @RequestMapping("/getinfo")
    public String getInfo(@RequestParam(value = "openId",defaultValue = "null")String openId){
        StudentEntity studentEntity = studentRepository.findByOpenId(openId).get(0);
        String jonId = studentEntity.getJobId();
        String name = studentEntity.getName();
        UserInfoBean userInfoBean = new UserInfoBean(jonId,name);
        Gson gson = new Gson();
        return gson.toJson(userInfoBean);
    }

    @RequestMapping("/setinfo")
    public String setInfo(@RequestParam(value = "openId",defaultValue = "null")String openId,
                          @RequestParam(value = "name",defaultValue = "null")String name,
                          @RequestParam(value = "jobId",defaultValue = "null")String jobId){
        StudentEntity studentEntity = studentRepository.findByOpenId(openId).get(0);
        studentEntity.setJobId(jobId);
        studentEntity.setName(name);
        studentRepository.save(studentEntity);
        return "";
    }

    @RequestMapping("/getlocation")
    public String getLocation(@RequestParam(value = "classId",defaultValue = "0")long classId){
        HashMap<Long,CheckedInfo> checkMap = TeacherController.checkMap;
        LocationBean locationBean = new LocationBean();
        if (checkMap.containsKey(classId)){
            locationBean.isValid = true;
            locationBean.latitude = checkMap.get(classId).latitude;
            locationBean.longitude = checkMap.get(classId).longitude;
        }else{
            locationBean.isValid = false;
        }
        Gson gson = new Gson();
        String res = gson.toJson(locationBean);
        return res;
    }

    @RequestMapping("/checkin")
    public String checkIn(@RequestParam(value = "openId",defaultValue = "null")String openId,
                          @RequestParam(value = "classId",defaultValue = "0")long classId,
                          @RequestParam(value = "latitude",defaultValue = "0")double latitude,
                          @RequestParam(value = "longitude",defaultValue = "0")double longitude){
        HashMap<Long,CheckedInfo> checkMap = TeacherController.checkMap;
        if (!checkMap.containsKey(classId)) return "不在签到时段";
        CheckedInfo checkedInfo = checkMap.get(classId);
        double t_latitude = checkedInfo.latitude;
        double t_longitude = checkedInfo.longitude;
        double dis = PositionUtil.distance_gcj02(latitude,longitude,t_latitude,t_longitude);
        if (dis>30) return "签到失败,签到最大距离20m,当前距离"+dis+"m";
        else{
            if (checkedInfo.checkedId.contains(openId)) return "您已签到";
            else{
                checkedInfo.checkedId.add(openId);
                return "签到成功,当前距离"+dis+"m";
            }
        }
    }
}
