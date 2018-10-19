package at.fhhgb.breakout

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

class ColorComponent(val color: Color) : Component

class TransformComponent(val position: Vector2) : Component

class SizeComponent(val width: Float, val height: Float) : Component