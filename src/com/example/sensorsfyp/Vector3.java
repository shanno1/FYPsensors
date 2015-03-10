package com.example.sensorsfyp;


public class Vector3 {

  private final float x;
  private final float y;
  private final float z;
  
  public  Vector3(float[] xyz){
	  this.x = xyz[0];
	  this.y = xyz[1];
	  this.z = xyz[2]; 
  }


  public Vector3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vector3 add(Vector3 other) {
    return new  Vector3(x + other.x, y + other.y, z + other.z);
  }

  public Vector3 subtract (Vector3 other) {
    return new Vector3(x - other.x, y - other.y, z - other.z);
  }

  public float dotProduct( Vector3 other) {
    return x * other.x + y * other.y + z * other.z;
  }

  public Vector3 normalize() {
    return new  Vector3(x / getMagnitude(), y / getMagnitude(), z / getMagnitude());
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public float getZ() {
    return z;
  }

  public float getMagnitude() {
    return (float) Math.sqrt(x * x + y * y + z * z);
  }

  @Override
  public String toString() {
    return String.format("Vector3<x: %.4f, y: %.4f, z: %.4f>", x, y, z);
  }

  @Override
  public int hashCode() {
    // Ensure that -0 and 0 are considered equal.
	  float x = this.x == 0 ? 0 : this.x;
	  float y = this.y == 0 ? 0 : this.y;
	  float z = this.z == 0 ? 0 : this.z;
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(z);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Vector3 other = (Vector3) obj;
    // Ensure that -0 and 0 are considered equal.
    float x = this.x == 0 ? 0 : this.x;
    float y = this.y == 0 ? 0 : this.y;
    float z = this.z == 0 ? 0 : this.z;
    float otherX = other.x == 0 ? 0 : other.x;
    float otherY = other.y == 0 ? 0 : other.y;
    float otherZ = other.z == 0 ? 0 : other.z;
    if (Double.doubleToLongBits(x) != Double.doubleToLongBits(otherX))
      return false;
    if (Double.doubleToLongBits(y) != Double.doubleToLongBits(otherY))
      return false;
    if (Double.doubleToLongBits(z) != Double.doubleToLongBits(otherZ))
      return false;
    return true;
  }
}