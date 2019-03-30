package com.cloudlyo.Controllers;

import com.cloudlyo.DataEntity.*;
import com.cloudlyo.JsonBean.ClassListBean;
import com.cloudlyo.JsonBean.UserInfoBean;
import com.cloudlyo.JsonBean.studentListBean;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    ClassRepository classRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CheckInRepository checkInRepository;
    @Autowired
    StudentRepository studentRepository;
    public static final HashMap<Long,CheckedInfo> checkMap = new HashMap<>();
    public static Thread checkThread;

    @RequestMapping("/getclass")
    public String getClass(@RequestParam(value = "openId",defaultValue = "null")String openId){
        ArrayList<ClassListBean> clas = new ArrayList<>();
        TeacherEntity teacher = teacherRepository.findByOpenId(openId).get(0);
        String[] clastr = teacher.getClasses().split("_");
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

    @RequestMapping("/addclass")
    public String addClass(@RequestParam(value = "name", defaultValue = "null") String name,
                           @RequestParam(value = "openId", defaultValue = "null") String openId){

        long classId;
        while(true){
            classId = (long)(Math.random()*1000000);
            if (classRepository.findById(classId)==null||classRepository.findById(classId).size()<=0) break;
        }

        ClassEntity cla = new ClassEntity();
        cla.setName(name);
        cla.setTeacherId(openId);
        cla.setId(classId);
        classRepository.save(cla);

        TeacherEntity teacher = teacherRepository.findByOpenId(openId).get(0);
        teacher.setClasses(teacher.getClasses()+"_"+classId);
        teacherRepository.save(teacher);

        return classId+"";
    }

    @RequestMapping("/getstudent")
    public String getStudent(@RequestParam(value = "classId",defaultValue = "null")long classId){
        ArrayList<studentListBean> studentList = new ArrayList<>();

        HashSet<String> set_checked;
        if (checkMap.containsKey(classId)) set_checked = checkMap.get(classId).checkedId;
        else{
            set_checked = new HashSet<>();
            List<CheckInEntity> Checks = checkInRepository.findByClassIdOrderByStartTimeDesc(classId);
            String recentCheck="";
            if (Checks!=null&&Checks.size()>0) recentCheck = Checks.get(0).getCheckedId();
            String[] str_checked = recentCheck.split("_");
            for (String openId:str_checked){
                if (!openId.equals("")) set_checked.add(openId);
            }
        }

        String[] studentsOpenId = classRepository.findById(classId).get(0).getStudents().split("_");

        for (String openId:studentsOpenId){
            System.out.println(openId);
            if (openId.equals("")) continue;
            StudentEntity studentEntity = studentRepository.findByOpenId(openId).get(0);
            String name = studentEntity.getName();
            String jobId = studentEntity.getJobId();
            String isChecked = set_checked.contains(openId)? "y":"n";
            studentList.add(new studentListBean(name,jobId,isChecked));
        }
        Gson gson = new Gson();
        String res = gson.toJson(studentList);
        return res;
    }

    @RequestMapping("/getinfo")
    public String getInfo(@RequestParam(value = "openId",defaultValue = "null")String openId){
        TeacherEntity teacherEntity = teacherRepository.findByOpenId(openId).get(0);
        String jonId = teacherEntity.getJobId();
        String name = teacherEntity.getName();
        UserInfoBean userInfoBean = new UserInfoBean(jonId,name);
        Gson gson = new Gson();
        return gson.toJson(userInfoBean);
    }

    @RequestMapping("/setinfo")
    public String setInfo(@RequestParam(value = "openId",defaultValue = "null")String openId,
                          @RequestParam(value = "name",defaultValue = "null")String name,
                          @RequestParam(value = "jobId",defaultValue = "null")String jobId){
        TeacherEntity teacherEntity = teacherRepository.findByOpenId(openId).get(0);
        teacherEntity.setJobId(jobId);
        teacherEntity.setName(name);
        teacherRepository.save(teacherEntity);
        return "";
    }

    @RequestMapping("/checkin")
    public String checkIn(@RequestParam(value = "classId",defaultValue = "null")long classId,
                          @RequestParam(value = "type",defaultValue = "null")String type,
                          @RequestParam(value = "lastTime",defaultValue = "null")int min,
                          @RequestParam(value = "latitude",defaultValue = "0")double latitude,
                          @RequestParam(value = "longitude",defaultValue = "0")double longitude){
        checkThread();
        if (checkMap.containsKey(classId)) return "上次签到未结束,无法发起新签到";
        CheckedInfo checkedInfo = new CheckedInfo(new HashSet<>(),type,new Date(), min,latitude,longitude);
        checkMap.put(classId,checkedInfo);
        return "发起签到成功";
    }

    @RequestMapping("/test")
    public String test(){
        HashSet<Long> set = new HashSet<>();
        set.add(new Long(100));
        return set.contains(new Long(100))+"";
    }

    @RequestMapping("/deleteclass")
    public String deleteClass(@RequestParam(value = "classId",defaultValue = "0")long classId){
        ClassEntity classEntity = classRepository.findById(classId).get(0);
        classRepository.deleteById(classId);

        String openId = classEntity.getTeacherId();
        TeacherEntity teacherEntity = teacherRepository.findByOpenId(openId).get(0);
        String[] classes = teacherEntity.getClasses().split("_");
        String newClasses = "";
        for (String cls:classes){
            if (!cls.equals("")&&!cls.equals(classId+"")) newClasses+=("_"+cls);
        }
        teacherEntity.setClasses(newClasses);
        teacherRepository.save(teacherEntity);

        String[] students = classEntity.getStudents().split("_");
        for (String stu:students){
            if (stu.equals("")) continue;
            StudentEntity studentEntity = studentRepository.findByOpenId(stu).get(0);
            classes = studentEntity.getClasses().split("_");
            newClasses="";
            for (String cls:classes){
                if (!cls.equals("")&&!cls.equals(classId+"")) newClasses+=("_"+cls);
            }
            studentEntity.setClasses(newClasses);
            studentRepository.save(studentEntity);
        }
        checkInRepository.deleteCheckInEntitiesByClassId(classId);
        return "";
    }

    public void checkThread(){
        if (checkThread==null){
            checkThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");;
                    while(true){
                        for(Long key: checkMap.keySet()){
                            CheckedInfo info = checkMap.get(key);
                            Date now = new Date();
                            if (new Date(info.startTime.getTime()+info.lastMin*60*1000).before(now)){
                                String checkedid = "";
                                for (String s:info.checkedId){
                                    checkedid += ("_"+s);
                                }
                                CheckInEntity checkInEntity = new CheckInEntity(key,df.format(info.startTime),info.lastMin,checkedid);
                                checkInRepository.save(checkInEntity);
                                checkMap.remove(key);
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            checkThread.start();
        }
    }

}
class CheckedInfo{
    HashSet<String> checkedId;
    String type;
    Date startTime;
    int lastMin;
    double latitude;
    double longitude;
    public CheckedInfo(HashSet<String> checkedId, String type, Date startTime, int lastMin) {
        this.checkedId = checkedId;
        this.type = type;
        this.startTime = startTime;
        this.lastMin = lastMin;
    }

    public CheckedInfo(HashSet<String> checkedId, String type, Date startTime, int lastMin, double latitude, double longitude) {
        this.checkedId = checkedId;
        this.type = type;
        this.startTime = startTime;
        this.lastMin = lastMin;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
