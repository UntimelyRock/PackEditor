package GUI;

import javax.swing.*;

public class Display extends JFrame implements Runnable {
    private static final long serialVersionUID = 1l;
    private Thread thread;
    private boolean isRunning = false;




    public Display(LaunchConfig launchConfig){
        Dimension size = new Dimension(launchConfig.tallness, launchConfig.width);
        this.setPreferredSize(size);
        this.setTitle(launchConfig.title);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton button1 = new JButton("Button 1");
        button1.setMaximumSize(new Dimension(10,10));
        JButton button2 = new JButton("Button 2");
        this.add(button1);
        this.add(button2);

    }

    public synchronized void start(){
        this.isRunning = true;
        this.thread = new Thread(this, "Display");
        this.thread.start();
    }

    public synchronized void stop(){
        this.isRunning = false;
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {


    }
}
