package at.fhhgb.breakout

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Shape

class ColorComponent(val color: Color) : Component

class TransformComponent(val position: Vector2) : Component

class SizeComponent(val width: Float, val height: Float) : Component

class PhysicsComponent(val body: Body) : Component

class ShapeComponent(val shape: Shape.Type) : Component

class PlayerComponent : Component

class CollisionComponent(var collisionEntity:Entity) : Component

class BallComponent : Component

class BricksComponent : Component