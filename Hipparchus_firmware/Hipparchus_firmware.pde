#include <ps2.h>

//Serial communication vars
char cmd[10];
int index;

double Setpoint, Input, Output;

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

MouseInfo mouseInfo;
MouseInfo mouseInfo2;

double previousPosition;
double tmp;
int spd;

void setup()
{
  pinMode(motorApinA, OUTPUT);
  pinMode(motorApinB, OUTPUT);
  pinMode(motorBpinA, OUTPUT);
  pinMode(motorBpinB, OUTPUT);

  Serial.begin(9600);

  Input = 0;//the current position
  Setpoint = 0;//the target position

  mouse.init();
  mouse2.init();

  goTo = false;
  sendPosition = false;
}

void loop()
{
  /************************ Mouse routines **************************/
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
  cmd[index] = 0;

  if (index > 0)
  {
    if (cmd[0] == 'G'){
      Setpoint = atoi(cmd+1);
      previousPosition = yPosition;
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
  /************************** Serial communication end ************************/
  /****************************************************************************/
  /******************************* Go-To/Tracking *****************************/
  tmp = yPosition;
  if ((abs(tmp - previousPosition) <= 10) || (abs(Setpoint - tmp) <= 10))
  {
    spd = 50;
  }
  else if ((abs(tmp - previousPosition) <= 20) || (abs(Setpoint - tmp) <= 20))
  {
    spd = 100;
  }
  else if ((abs(tmp - previousPosition) <= 30) || (abs(Setpoint - tmp) <= 30))
  {
    spd = 150;
  }
  else if ((abs(tmp - previousPosition) <= 40) || (abs(Setpoint - tmp) <= 40))
  {
    spd = 200;
  }
  else
  {
    spd = 250;
  }
  if (goTo)
  {
    Input = yPosition;
    if (abs(Setpoint - Input) != 0)
    {
      if (Setpoint < Input)
      {
        motorABack(spd);
      }
      if (Setpoint > Input)
      {
        motorAForward(spd);
      }
    }
    else
    {
      motorAStop();
      goTo = false;
      sendPosition = true;
    }
    /********************************** End goto ******************************/
    /**************************************************************************/
  }
  if (sendPosition)
  {
    Serial.print("G:xy:");
    Serial.print(xPosition);
    Serial.print(":");
    Serial.print(yPosition);
    Serial.print("\n");
    delay(200);
    sendPosition = false;
  }
}

/***************************** Motor control commands **************************/
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






