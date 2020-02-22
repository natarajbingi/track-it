package com.a.goldtrack.Model;

import java.util.List;

/**
 * Created by nataraj-pc on 15-Mar-18.
 */

public class TeacherLoginRes {
    public Data data;
    public String response;
    public Boolean success;


    public class Data {
        public String deviceRegistrationID;
        public String userName;
        public int institutionID;
        public int teacherID;
        public String emailID;
        public String mobileNo;
        public String firstName;
        public String lastName;
        public List<SubjectList> subjectList;//
        public List<SectionList> sectionList;//
        public String institutionName;
        public List<ClassList> classList;//
        public List<StudentList> studentList;//
    }

    public class SectionList {
        public int sectionID;
        public String sectionName;
    }
    public class SubjectList {
        public int subjectID;
        public String subjectName;
    }

    public class ClassList {
        public int classID;
        public String className;
        public String sectionName;
        public List<String> subjectIDs;
    }

    public class StudentList {
        public String rollNo;
        public String profilePicPath;
        public String studentFirstName;
        public String studentLastName;
        public int classID;
        public int studentID;
        public int sectionID;
        public String gender;
    }
}
