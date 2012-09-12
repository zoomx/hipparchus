#include <ps2.h>

char cmd[20];
int index = 0;

int Setpoint1 = 0;
int Input1 = 0;
int Output1 = 0;
int Setpoint2 = 0;
int Input2 = 0;
int Output2 = 0;

int motorApinA = 3;
int motorApinB = 5;
int motorBpinA = 9;
int motorBpinB = 11;

int spdLeft = 0;
int spdRight = 0;
int spdUp = 0;
int spdDown = 0;

//Mouse initialisation. PS2Mouse(clock, data)
PS2Mouse mouse(12, 7);
PS2Mouse mouse2(8, 2);

boolean goTo1 = false;
boolean goTo2 = false;
boolean sendPosition = false;
boolean moveRight = false;
boolean moveLeft = false;
boolean moveUp = false;
boolean moveDown = false;

MouseInfo mouseInfo;
MouseInfo mouseInfo2;

int xPosition = 0;
int yPosition = 0;
int previousPosition1 = 0;
int previousPosition2 = 0;
int tmp1 = 0;
int tmp2 = 0;
int spd1 = 0;
int spd2 = 0;

void setup()
{
  Serial.begin(115200);

  pinMode(motorApinA, OUTPUT);
  pinMode(motorApinB, OUTPUT);
  pinMode(motorBpinA, OUTPUT);
  pinMode(motorBpinB, OUTPUT);

  delay(2000);//wait for mouse to open up

  mouse.init();
  mouse2.init();

}
void loop()
{
  while (Serial.available())
  {
    cmd[index] = Serial.read();
    delay(1);    
    index++;    
  }
  cmd[index] = '\0';
  

  /************************ Mouse routines **************************/
  mouse.getData(&mouseInfo);
  mouse2.getData(&mouseInfo2);
  xPosition = mouseInfo.cX;
  yPosition = mouseInfo2.cX;
  /******************************************************************/
  if (index > 0)
  {    
    //Goes to specific set points: G100,200
    if (cmd[0] == 'G'){
      Setpoint2 = atoi(strtok((cmd+1), ","));
      Setpoint1 = atoi(strtok(NULL, ","));

      previousPosition1 = yPosition;
      previousPosition2 = xPosition;
      goTo1 = true;
      goTo2 = true;
    }
    //Stops the motors
    if (cmd[0] == 'S')
    {
      goTo1 = false;
      goTo2 = false;
      sendPosition = false;
    }
    //Transmits the current position
    if (cmd[0] == 'T')
    {
      Serial.print("G:xyt:");
      Serial.print(xPosition);
      Serial.print(":");
      Serial.print(yPosition);
      Serial.print("\n");
    }
    //Resets the mouse
    if (cmd[0] == 'R')
    {
      mouse.reset();
      mouse2.reset();
    }
    //Manual moving
    if (cmd[0] == 'r')
    {
      moveRight = true;
    }
    if (cmd[0] == 'r' && cmd[1] == 's')
    {
      moveRight = false;
    }    
    if (cmd[0] == 'l')
    {
      moveLeft = true;
    }
    if (cmd[0] == 'l' && cmd[1] == 's')
    {
      moveLeft = false;
    }
    
    
    
    if (cmd[0] == 'u')
    {
      moveUp = true;
    }
    if (cmd[0] == 'u' && cmd[1] == 's')
    {
      moveUp = false;
    }    
    if (cmd[0] == 'd')
    {
      moveDown = true;
    }
    if (cmd[0] == 'd' && cmd[1] == 's')
    {
      moveDown = false;
    }
    index = 0;
  }

  if (moveRight && spdLeft == 0)
  {
    if (spdRight < 250)
    {
      spdRight += 25;
      motorAForward(spdRight);
      delay(100);
    }
  }
  if (!moveRight)
  {
    if(spdRight > 0)
    {
      spdRight -= 25;
      motorAForward(spdRight);
      delay(100);
    }
  }
  if (moveLeft && spdRight == 0)
  {
    if (spdLeft < 250)
    {
      spdLeft += 25;
      motorABack(spdLeft);
      delay(100);
    }
  }
  if (!moveLeft)
  {
    if(spdLeft > 0)
    {
      spdLeft -= 25;
      motorABack(spdLeft);
      delay(100);
    }
  }
  
  
  
  
  
  if (moveUp && spdDown == 0)
  {
    if (spdUp < 250)
    {
      spdUp += 25;
      motorBForward(spdUp);
      delay(100);
    }
  }
  if (!moveUp)
  {
    if(spdUp > 0)
    {
      spdUp -= 25;
      motorBForward(spdUp);
      delay(100);
    }
  }
  if (moveDown && spdUp == 0)
  {
    if (spdDown < 250)
    {
      spdDown += 25;
      motorBBack(spdDown);
      delay(100);
    }
  }
  if (!moveDown)
  {
    if(spdDown > 0)
    {
      spdDown -= 25;
      motorBBack(spdDown);
      delay(100);
    }
  }



  /************************** Serial communication end ************************/
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









