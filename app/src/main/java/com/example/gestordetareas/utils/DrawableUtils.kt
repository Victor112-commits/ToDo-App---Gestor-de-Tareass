package com.example.gestordetareas.utils

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.core.graphics.ColorUtils

/**
 * Utilidades para crear dibujos personalizados
 * Proporciona métodos para crear formas y decoraciones
 */
object DrawableUtils {
    
    /**
     * Crear un dibujo circular con color sólido
     * @param color Color del círculo
     * @param radius Radio del círculo en dp
     * @return GradientDrawable configurado como círculo
     */
    fun createCircleDrawable(color: Int, radius: Float): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            setSize(
                (radius * 2).toInt(),
                (radius * 2).toInt()
            )
        }
    }
    
    /**
     * Crear un dibujo rectangular con bordes redondeados
     * @param color Color de fondo
     * @param cornerRadius Radio de las esquinas en dp
     * @return GradientDrawable configurado como rectángulo redondeado
     */
    fun createRoundedRectangleDrawable(color: Int, cornerRadius: Float): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            this.cornerRadius = cornerRadius
        }
    }
    
    /**
     * Crear un dibujo de borde con color sólido
     * @param borderColor Color del borde
     * @param backgroundColor Color de fondo
     * @param borderWidth Grosor del borde en dp
     * @param cornerRadius Radio de las esquinas en dp
     * @return GradientDrawable configurado como borde
     */
    fun createBorderDrawable(
        borderColor: Int,
        backgroundColor: Int = Color.TRANSPARENT,
        borderWidth: Float = 2f,
        cornerRadius: Float = 8f
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            setStroke(borderWidth.toInt(), borderColor)
            this.cornerRadius = cornerRadius
        }
    }
    
    /**
     * Crear un color con transparencia
     * @param color Color base
     * @param alpha Valor de transparencia (0-255)
     * @return Color con transparencia aplicada
     */
    fun createColorWithAlpha(color: Int, alpha: Int): Int {
        return ColorUtils.setAlphaComponent(color, alpha)
    }
    
    /**
     * Crear un gradiente lineal
     * @param startColor Color inicial
     * @param endColor Color final
     * @param orientation Orientación del gradiente
     * @return GradientDrawable configurado como gradiente
     */
    fun createLinearGradientDrawable(
        startColor: Int,
        endColor: Int,
        orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM
    ): GradientDrawable {
        return GradientDrawable().apply {
            this.orientation = orientation
            colors = intArrayOf(startColor, endColor)
        }
    }
}
