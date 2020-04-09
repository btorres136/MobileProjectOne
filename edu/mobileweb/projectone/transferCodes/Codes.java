package edu.mobileweb.projectone.transferCodes;

/**
 * <h3>Codes Class</h3>
 * <p>This class contains the rules and codes to be 
 * implemented to sucessfully communicate between the
 * client and the server.</p>
 * @author Brian D. Torres Alvarado
 * @author Joel E. Martinez
 */
public class Codes {
    public static final int BUFFER_SIZE = 1024;
    public static final int OK = 1;
    public static final int SEETABLE = 2;
    public static final int PUTPIECE = 3;
    public static final int CANTBEPLAY = 4;
    public static final int CLOSECONNECTION = 5;
    public static final int SENDPIECE = 6;
    public static final int TURN = 7;
    public static final int UPDATE = 8;
    public static final int LEFT = 9;
    public static final int RIGHT = 10;
    public static final int PASS = 11;
    public static final int NOP = 20;
    public static final int WRONGCOMMAND = 30;
    
}