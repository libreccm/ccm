package com.arsdigita.cms.dabin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Jens Pelzetter
 */
public class ProjectData {

    private String dabinId;
    private String nameDe;
    private String nameEn;
    private String department;
    private List<MembershipData> members;
    private Calendar begin;
    private Calendar end;
    private String descDe;
    private String descEn;
    private String fundingDe;
    private String fundingEn;
    private String link;

    public ProjectData() {
        members = new ArrayList<MembershipData>();
    }

    public Calendar getBegin() {
        return begin;
    }

    public void setBegin(Calendar begin) {
        this.begin = begin;
    }

    public String getDabinId() {
        return dabinId;
    }

    public void setDabinId(String dabinId) {
        this.dabinId = dabinId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescDe() {
        return descDe;
    }

    public void setDescDe(String desc) {
        this.descDe = desc;
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEn(String descEn) {
        this.descEn = descEn;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }

    public String getFundingDe() {
        return fundingDe;
    }

    public void setFundingDe(String funding) {
        this.fundingDe = funding;
    }

    public String getFundingEn() {
        return fundingEn;
    }

    public void setFundingEn(String fundingEn) {
        this.fundingEn = fundingEn;
    }

    public void addMember(MembershipData member) {
        members.add(member);
    }

    public List<MembershipData> getMembers() {
        return members;
    }

    public void setMembers(List<MembershipData> members) {
        this.members = members;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
