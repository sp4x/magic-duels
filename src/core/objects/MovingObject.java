/**
 * 
 */
package core.objects;

import core.space.Direction;
import core.space.Position;
import core.space.World;

/**
 * @author spax
 * An abstract object that can moves throw a direction at a given speed
 *
 */
public abstract class MovingObject extends AbstractObject {

	Direction direction;

	Position lastPosition;
	boolean collides = false;

	float speed = 1;

	public MovingObject() {
		this.direction = new Direction();
	}


	public void lookAt(AbstractObject obj) {
		float angle = this.position.angleBetween(obj.position);
		this.direction = new Direction(angle);
	}


	public Direction getDirection() {
		return direction;
	}



	public void setDirection(Direction direction) {
		this.direction = direction;
	}


	public void setSpeed(float speed) {
		this.speed = speed;
	}


	public void moveForward() {
		this.moveThrough(this.direction);
	}

	public void moveBackward() {
		this.moveThrough(this.direction.opposite());
	}

	public void moveLeft() {
		float angle = (float) -Math.PI/2;
		this.moveThrough(this.direction.rotate(angle));
	}

	public void moveRight() {
		float angle = (float) Math.PI/2;
		this.moveThrough(this.direction.rotate(angle));
	}

	public void moveALeft() {
		Direction d = Direction.yDirection();
		if (direction.getX()>0)
			d = d.opposite();
		this.moveThrough(d);
	}

	public void moveARight() {
		Direction d = Direction.yDirection();
		if (direction.getX()<0)
			d = d.opposite();
		this.moveThrough(d);
	}


	protected void moveThrough(Direction d) {
		this.lastPosition = getPosition();
		float x = d.getX() * speed;
		float y = d.getY() * speed;
		this.getPosition().move(x, y);
		World.checkCollisions(this);
		if (collides) {
			this.setPosition(lastPosition);
			collides = false;
		}
	}

	@Override
	public void handleCollision(AbstractObject other) {
		collides = true;
	}


}
