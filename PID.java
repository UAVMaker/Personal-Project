/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Darvin
 */
public class PID {

    static double kp, ki, kd;
    static double Input = 0, Output, Setpoint = 0, timeChange;
    static double iTerm = 0, lastInput = 0;

    static long lastTime, now;
    static int sampleTime = 1000; // 1 second
    static double outMax, outMin;
    static boolean inAuto = false;
    static final int Manual = 0, Automatic = 1;
    static final int DIRECT = 0, REVERSE = 1;
    static int controllerDirection = DIRECT;

    public static void compute() {
        //set the values first
       if(!inAuto)return;
       
        

        // time differences
        now = getMillis();
    
        timeChange = (double) (now - lastTime);
        
        if(timeChange >= sampleTime){
        // calculate all the errors
        double error = Setpoint - Input;
        iTerm += (error * ki) ;
        
        // is the iTerm in the range
        if(iTerm > outMax) iTerm = outMax;
        else if(iTerm < outMin) iTerm = outMin;
        
        double dInput = (Input - lastInput);

        //Output 
        Output = kp * error + iTerm + kd * dInput;
        if(Output > outMax) Output = outMax;
        else if(Output < outMin) Output = outMin;
        
        //Time for next iteration
        lastInput = Input;
        lastTime = now;

    }}

    public static void setPID(double Kp, double Ki, double Kd) {
        if(Kp <0 || Ki < 0 || Kd <0) return;
        
        double sampleTimeInSeconds = ((double) sampleTime)/1000;
        kp = Kp;
        ki = Ki * sampleTimeInSeconds;
        kd = Kd / sampleTimeInSeconds;
        
        if(controllerDirection == REVERSE){
            kp = (0 - kp);
            ki = (0 - ki);
            kd = (0 - kd);
        }
    }

    public static void SetSampleTime(int NewSampleTime){
        if(NewSampleTime >0){
        double ratio = (double)NewSampleTime / (double)sampleTime;
        
        ki *= ratio;
        kd /= ratio;
        sampleTime = (int)NewSampleTime;
        }
    
    }
    
    public  static void SetOutputLimits(double Min, double Max){
        if(Min > Max)return;
        outMin = Min;
        outMax = Max;
        
        if(Output > outMax) Output = outMax;
        else if(Output < outMin) Output = outMin;
        
        if(iTerm > outMax) iTerm = outMax;
        else if(iTerm < outMin) iTerm = outMin;
    
    
    }
    
    private static long getMillis() {
        return System.currentTimeMillis();
    }
    
    public static void SetMode(int Mode){
    boolean newAuto = (Mode == Automatic);
    if(newAuto && !inAuto){
    Initialize();
    }
    }

    public static void SetDirection(int Direction){
        controllerDirection = Direction;
    }
    private static void Initialize() {
        lastInput = Input;
        iTerm = Output;
        if(iTerm > outMax)iTerm = outMax;
        else if(iTerm < outMin) iTerm = outMin;
    }
}
