/**
 * Copyright 2020 bejson.com
 */
package com.eg.libraryappserver.book;

import java.util.Date;

/**
 * 通过查询这个网址得到
 * http://60.218.184.234:8091/opac/api/holding/127796?limitLibcodes=
 * 里面有条码号
 */
public class Holding {

    private long recno;
    private long bookrecno;
    private int state;
    private String barcode;
    private String callno;
    private String orglib;
    private String orglocal;
    private String curlib;
    private String curlocal;
    private String cirtype;
    private Date regdate;
    private Date indate;
    private double singlePrice;
    private double totalPrice;
    private int totalLoanNum;
    private int totalResNum;
    private int totalRenewNum;
    private int totalLibNum;
    private int volnum;
    private String volInfo;
    private String memoinfo;
    private String shelfno;
    private String regno;
    private String biblios;
    private String loan;
    private String packageno;
    private String stateStr;

    public void setRecno(long recno) {
        this.recno = recno;
    }

    public long getRecno() {
        return recno;
    }

    public void setBookrecno(long bookrecno) {
        this.bookrecno = bookrecno;
    }

    public long getBookrecno() {
        return bookrecno;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setCallno(String callno) {
        this.callno = callno;
    }

    public String getCallno() {
        return callno;
    }

    public void setOrglib(String orglib) {
        this.orglib = orglib;
    }

    public String getOrglib() {
        return orglib;
    }

    public void setOrglocal(String orglocal) {
        this.orglocal = orglocal;
    }

    public String getOrglocal() {
        return orglocal;
    }

    public void setCurlib(String curlib) {
        this.curlib = curlib;
    }

    public String getCurlib() {
        return curlib;
    }

    public void setCurlocal(String curlocal) {
        this.curlocal = curlocal;
    }

    public String getCurlocal() {
        return curlocal;
    }

    public void setCirtype(String cirtype) {
        this.cirtype = cirtype;
    }

    public String getCirtype() {
        return cirtype;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setIndate(Date indate) {
        this.indate = indate;
    }

    public Date getIndate() {
        return indate;
    }

    public void setSinglePrice(double singlePrice) {
        this.singlePrice = singlePrice;
    }

    public double getSinglePrice() {
        return singlePrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalLoanNum(int totalLoanNum) {
        this.totalLoanNum = totalLoanNum;
    }

    public int getTotalLoanNum() {
        return totalLoanNum;
    }

    public void setTotalResNum(int totalResNum) {
        this.totalResNum = totalResNum;
    }

    public int getTotalResNum() {
        return totalResNum;
    }

    public void setTotalRenewNum(int totalRenewNum) {
        this.totalRenewNum = totalRenewNum;
    }

    public int getTotalRenewNum() {
        return totalRenewNum;
    }

    public void setTotalLibNum(int totalLibNum) {
        this.totalLibNum = totalLibNum;
    }

    public int getTotalLibNum() {
        return totalLibNum;
    }

    public void setVolnum(int volnum) {
        this.volnum = volnum;
    }

    public int getVolnum() {
        return volnum;
    }

    public void setVolInfo(String volInfo) {
        this.volInfo = volInfo;
    }

    public String getVolInfo() {
        return volInfo;
    }

    public void setMemoinfo(String memoinfo) {
        this.memoinfo = memoinfo;
    }

    public String getMemoinfo() {
        return memoinfo;
    }

    public void setShelfno(String shelfno) {
        this.shelfno = shelfno;
    }

    public String getShelfno() {
        return shelfno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getRegno() {
        return regno;
    }

    public void setBiblios(String biblios) {
        this.biblios = biblios;
    }

    public String getBiblios() {
        return biblios;
    }

    public void setLoan(String loan) {
        this.loan = loan;
    }

    public String getLoan() {
        return loan;
    }

    public void setPackageno(String packageno) {
        this.packageno = packageno;
    }

    public String getPackageno() {
        return packageno;
    }

    public void setStateStr(String stateStr) {
        this.stateStr = stateStr;
    }

    public String getStateStr() {
        return stateStr;
    }

}