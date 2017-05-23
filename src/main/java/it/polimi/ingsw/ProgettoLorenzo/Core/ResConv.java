package it.polimi.ingsw.ProgettoLorenzo.Core;


public class ResConv {
    private int index;
    private Resources resSrc;
    private Resources resDst;
    private int councDst;

    public ResConv(int index, Resources resSrc, Resources resDst) {
        this.index = index;
        this.resSrc = resSrc;
        this.resDst = resDst;
    }

    public ResConv(int index, Resources resSrc, int councDst) {
        this.index = index;
        this.resSrc = resSrc;
        this.councDst = councDst;
    }

    public int getIndex() {
        return index;
    }

    public Resources getResSrc() {
        return resSrc;
    }

    public Resources getResDst() {
        return resDst;
    }

    public int getCouncDst() {
        return councDst;
    }

    @Override
    public String toString() {
        String out = new String();
        if (this.resDst != null) {
            out = String.valueOf(this.index)+ ": " +this.resSrc+ " -> " +this.resDst;
        }
        else if (this.councDst != 0) {
            out = String.valueOf(this.index)+ ": " +this.resSrc+ " -> CouncilPrivilege: " +String.valueOf(this.councDst);
        }
        return out;
    }
}
