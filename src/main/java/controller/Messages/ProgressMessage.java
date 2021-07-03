package controller.Messages;

public class ProgressMessage {
    private double progress;

    public ProgressMessage(double progress) {
        this.progress = progress;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
