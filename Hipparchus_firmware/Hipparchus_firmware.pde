#include <ps2.h>

//Serial communication vars
char cmd[20];
int index;

int Setpoint1, Input1, Output1;
int Setpoint2, Input2, Output2;

// Motor's pins
int motorApinA = 3;
int motorApinB = 5;
int motorBpinA = 9;
int motorBpinB = 11;

//Mouse initialisation. PS2Mouse(clock, data)
PS2Mouse mouse(12, 7);
PS2Mouse mouse2(8, 2);

boolean goTo1;
boolean goTo2;
boolean sendPosition;

MouseInfo mouseInfo;
MouseInfo mouseInfo2;

int xPosition;
int yPosition;
int previousPosition1;
int previousPosition2;
int tmp1;
int tmp2;
int spd1;
int spd2;

void setup()
{
  pinMode(motorApinA, OUTPUT);
  pinMode(motorApinB, OUTPUT);
  pinMode(motorBpinA, OUTPUT);
  pinMode(motorBpinB, OUTPUT);

  Serial.begin(9600);

  xPosition = 0;
  yPosition = 0;
  
  Input1 = 0;
  Setpoint1 = 0;
  Input2 = 0;
  Setpoint2 = 0;

  mouse.init();
  mouse2.init();

  goTo1 = false;
  goTo2 = false;
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
      Setpoint2 = atoi(strtok((cmd+1), ","));
      Setpoint1 = atoi(strtok(NULL, ","));
      
      previousPosition1 = yPosition;
      previousPosition2 = xPosition;
      goTo1 = true;
      goTo2 = true;
    }
    if (cmd[0] == 'S')
    {
      goTo1 = false;
      goTo2 = false;
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
  tmp1 = yPosition;
  tmp2 = xPosition;
  if ((abs(tmp1 - previousPosition1) <= 10) || (abs(Setpoint1 - tmp1) <= 10))
  {
    spd1 = 50;
  }
  else if ((abs(tmp1 - previousPosition1) <= 20) || (abs(Setpoint1 - tmp1) <= 20))
  {
    spd1 = 100;
  }
  else if ((abs(tmp1 - previousPosition1) <= 30) || (abs(Setpoint1 - tmp1) <= 30))
  {
    spd1 = 150;
  }
  else if ((abs(tmp1 - previousPosition1) <= 40) || (abs(Setpoint1 - tmp1) <= 40))
  {
    spd1 = 200;
  }
  else
  {
    spd1 = 250;
  }
  
  if ((abs(tmp2 - previousPosition2) <= 10) || (abs(Setpoint2 - tmp2) <= 10))
  {
    spd2 = 50;
  }
  else if ((abs(tmp2 - previousPosition2) <= 20) || (abs(Setpoint2 - tmp2) <= 20))
  {
    spd2 = 100;
  }
  else if ((abs(tmp2 - previousPosition2) <= 30) || (abs(Setpoint2 - tmp2) <= 30))
  {
    spd2 = 150;
  }
  else if ((abs(tmp2 - previousPosition2) <= 40) || (abs(Setpoint2 - tmp2) <= 40))
  {
    spd2 = 200;
  }
  else
  {
    spd2 = 250;
  }
  if (goTo1)
  {
    Input1 = yPosition;
    if (abs(Setpoint1 - Input1) != 0)
    {
      if (Setpoint1 < Input1)
      {
        motorABack(spd1);
      }
      if (Setpoint1 > Input1)
      {
        motorAForward(spd1);
      }
    }
    else
    {
      motorAStop();
      goTo1 = false;
      sendPosition = true;
    }
  }
  if (goTo2)
  {
    Input2 = xPosition;
    if (abs(Setpoint2 - Input2) != 0)
    {
      if (Setpoint2 < Input2)
      {
        motorBBack(spd2);
      }
      if (Setpoint2 > Input2)
      {
        motorBForward(spd2);
      }
    }
    else
    {
      motorBStop();
      goTo2 = false;
      sendPosition = true;
    }
  }
  /***************************** Go-To/Tracking end ***************************/
  /****************************************************************************/

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
void motorBForward(int spd)
{
  digitalWrite(motorBpinA, HIGH);
  digitalWrite(motorBpinB, LOW);
  analogWrite(motorBpinA, spd);
}

void motorBBack(int spd)
{
  digitalWrite(motorBpinA, LOW);
  digitalWrite(motorBpinB, HIGH);
  analogWrite(motorBpinB, spd);
}

void motorBStop()
{
  digitalWrite(motorBpinA, LOW);
  digitalWrite(motorBpinB, LOW);
  analogWrite(motorBpinB, 0);
}
