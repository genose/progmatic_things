public class Eagle extends Bird implements Fly {

    private boolean flying;
    private int altitude;

    public Eagle(String name) {
        super(name);
        this.flying = false;
        this.altitude = 0;
    }

    public int getAltitude() {
        return altitude;
    }

    public boolean isFlying() {
        return flying;
    }

    @Override
    public String sing() {
        return "Screech!";
    }

    /**
     *
     */
    @Override
    public void takeOff() {
        this.flying = true;
    }

    /**
     * @param value
     */
    @Override
    public void ascend(int value) {
        this.altitude += Math.max(value,0);
    }

    /**
     *
     */
    @Override
    public void glide() {
        this.flying = true;
    }

    /**
     * @param value
     */
    @Override
    public void descend(int value) {
        this.altitude -= Math.max(value,0);
    }

    /**
     *
     */
    @Override
    public void land() {
        this.flying = false;
        this.altitude = 0;
    }
}
