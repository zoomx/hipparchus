#include <PID_v1.h>
#include <ps2.h>

//Serial communication vars
char cmd[10];
int index;

//PID vars
double Setpoint, Input, Output;
double Kp = 5;
double Ki = 0.5;
double Kd = 1;
PID myPID(&Input, &Output, &Setpoint,Kp,Ki,Kd, DIRECT);

// Motor's pins
int motorApinA = 3;
int motorApinB = 5;
int motorBpinA = 9;
int motorBpinB = 11;

double xPosition = 0;
double yPosition = 0;

//Mouse initialisation. PS2Mouse(clock, data)
PS2Mouse mouse(12, 7);
PS2Mouse mouse2(8, 2);

boolean goTo;
boolean sendPosition;

void setup()
{

  pinMode(motorApinA, OUTPUT);
  pinMode(motorApinB, OUTPUT);
  pinMode(motorBpinA, OUTPUT);
  pinMode(motorBpinB, OUTPUT);

  Serial.begin(9600);

/********************* PID initialisation *************************/
  Input = 0;//the current position
  Setpoint = 0;//the target position
  myPID.SetMode(AUTOMATIC);//turn on the pid


  mouse.init();
  mouse2.init();

  goTo = false;
  sendPosition = false;
}

void loop()
{
/******************Encoders initialisation*************************/
  MouseInfo mouseInfo;
  MouseInfo mouseInfo2;
  mouse.getData(&mouseInfo);
  mouse2.getData(&mouseInfo2);
  xPosition = mouseInfo.cX;
  yPosition = mouseInfo2.cY;
/******************************************************************/
  
/********************* Serial Communication ***********************/
  index = 0;
  //Get the serial command and store it to the char array
  while (Serial.available())
  {  
    cmd[index] = Serial.read();
    delay(1);
    index++;
  }
  //Null character in ASCII at the end of each line
  cmd[index] = 0;
  //Communication protocol
  if (index > 0)
  {
    if (cmd[0] == 'G'){
      Setpoint = atoi(cmd+1);
      goTo = true;
    }
    if (cmd[0] == 'S')
    {
      goTo = false;
      sendPosition = false;
    }
    if (cmd[0] == 'T')
    {
      Serial.print("G:xyt:");
      Serial.print(xPosition);
      Serial.print(":");
      Serial.print(yPosition);
      Serial.print("\n");
    }
    if (cmd[0] == 'R')
    {
      mouse.reset();
      mouse2.reset();
    }
  }
/*****************************************Serial communication end********************************/
  if (goTo)
  {
/*********************************************Go-To/Tracking**************************************/
    Input = yPosition;
    myPID.Compute();
    if (abs(Setpoint - Input) != 0)
    {
      if (Setpoint < Input)
      {
        myPID.SetControllerDirection(REVERSE);
        motorABack(Output);
      }
      if (Setpoint > Input)
      {
        myPID.SetControllerDirection(DIRECT);
        motorAForward(Output);
      }
    }
    else
    {
      motorAStop();
      goTo = false;
      sendPosition = true;
    }
    /**************************************************End goto**************************************/
    if (sendPosition)
    {
      Serial.print("G:xy:");
      Serial.print(xPosition);
      Serial.print(":");
      Serial.print(yPosition);
      Serial.print("\n");
      //delay(50);
      sendPosition = false;
    }
  }
  /*********************************************Go-To/Tracking**************************************
   * //Input = mouseInfo.cY;
   * Input = mouseY;
   * myPID.Compute();
   * if (Setpoint < Input){
   * myPID.SetControllerDirection(REVERSE);
   * motorABack(Output);
   * }
   * if (Setpoint > Input){
   * myPID.SetControllerDirection(DIRECT);
   * motorAForward(Output);
   * }
   * if (abs(Setpoint - Input) <= 6 ){
   * motorAStop();
   * }
  /**************************************************End goto**************************************/
}

/************************************************Motor control commands*********************************/
void motorAForward(int spd)
{
  digitalWrite(motorApinA, HIGH);
  digitalWrite(motorApinB, LOW);
  analogWrite(motorApinA, spd);
}

void motorABack(int spd)
{
  digitalWrite(motorApinA, LOW);
  digitalWrite(motorApinB, HIGH);
  analogWrite(motorApinB, spd);
}

void motorAStop()
{
  digitalWrite(motorApinA, LOW);
  digitalWrite(motorApinB, LOW);
  analogWrite(motorApinB, 0);
}
