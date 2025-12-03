package json;

public class JsonCell {
    public int ownedBy;
    public int unitID;
    public int state;

    public JsonCell(int ownedBy, int unitID, int state) {
        this.ownedBy = ownedBy;
        this.unitID = unitID;
        this.state = state;
    }
}
