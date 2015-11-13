package toucan.sunka;

/**
 * Created by andrei on 12/11/15.
 */
public class OnlineData {
    public boolean moveFinished;

    public OnlineData() {
    }

    public boolean moveFinished() {
        return moveFinished;
    }

    public void startMove(){
        this.moveFinished = false;
    }

    public void endMove(){
        this.moveFinished = true;
    }
}
