package at.fhhgb.breakout.systems

import at.fhhgb.breakout.*
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.google.inject.Inject

class RenderingSystem @Inject constructor(private val batch: SpriteBatch, private val camera: OrthographicCamera) :
        IteratingSystem(Family.all(ColorComponent::class.java, TransformComponent::class.java, SizeComponent::class.java, RenderComponent::class.java).get()) {
    private val shapeRenderer = ShapeRenderer()

    override fun update(deltaTime: Float) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        super.update(deltaTime)
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val color = entity.getComponent(ColorComponent::class.java).color
        val position = entity.getComponent(TransformComponent::class.java).position
        val size = entity.getComponent(SizeComponent::class.java)
        val renderType = entity.getComponent(RenderComponent::class.java).renderType
        val width = size.width
        val height = size.height

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = color
        when (renderType) {
            RenderType.Brick -> {
                if (entity.getComponent(BricksComponent::class.java).alive) {
                    shapeRenderer.rect(position.x - width / 2f, position.y - height / 2f, width, height)
                }
            }
            RenderType.Paddle -> {
                shapeRenderer.rect(position.x - width / 2f, position.y - height / 2f, width, height)
            }
            else -> {
                shapeRenderer.circle(position.x, position.y, width / 2f)
            }
        }
        shapeRenderer.end()
    }
}