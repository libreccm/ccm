package com.arsdigita.cms.dabin;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jens Pelzetter
 */
public class DepartmentData {

    private String dabinId;
    private String nameDe;
    private String nameEn;
    private List<MembershipData> members;

    public DepartmentData() {
        members = new ArrayList<MembershipData>();
    }

    public String getDabinId() {
        return dabinId;
    }

    public void setDabinId(String dabinId) {
        this.dabinId = dabinId;
    }

    public String getNameDe() {
        return nameDe;
    }

    public void setNameDe(String nameDe) {
        this.nameDe = nameDe;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public List<MembershipData> getMembers() {
        return members;
    }

    public void setMembers(List<MembershipData> members) {
        this.members = members;
    }

    public void addMember(MembershipData member) {
        members.add(member);
    }
}
