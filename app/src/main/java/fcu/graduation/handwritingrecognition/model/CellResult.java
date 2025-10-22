package fcu.graduation.handwritingrecognition.model;

public class CellResult {
    public float x;
    public float y;
    public float w;
    public float h;
    public float conf;
    public int cls;

    public CellResult(float x, float y, float w, float h, float conf, int cls) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.conf = conf;
        this.cls = cls;
    }

}
