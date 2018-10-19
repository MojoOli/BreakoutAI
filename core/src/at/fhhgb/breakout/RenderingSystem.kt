package at.fhhgb.breakout

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.google.inject.Inject

class RenderingSystem @Inject constructor(private val batch: SpriteBatch) : IteratingSystem(Family.all(ColorComponent::class.java, TransformComponent::class.java, SizeComponent::class.java).get()){
    private val shapeRenderer = ShapeRenderer()

    override fun update(deltaTime: Float) {
        batch.begin()
        super.update(deltaTime)
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val color = entity.getComponent(ColorComponent::class.java).color
        val position = entity.getComponent(TransformComponent::class.java).position
        val size = entity.getComponent(SizeComponent::class.java)
        val width = entity.getComponent(SizeComponent::class.java).width
        val height = entity.getComponent(SizeComponent::class.java).height

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = color
        shapeRenderer.rect(position.x + 0f, position.y + 0f, width, height)
        shapeRenderer.end()
    }
}