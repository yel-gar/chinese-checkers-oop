package json;

public class JsonCell {
    public int x;
    public int y;
    public int ownedBy;
    public int unitID;
    public int state;

    public JsonCell(int x, int y, int ownedBy, int unitID, int state) {
        this.x = x;
        this.y = y;
        this.ownedBy = ownedBy;
        this.unitID = unitID;
        this.state = state;
    }
}
