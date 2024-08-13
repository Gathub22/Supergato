package com.example.supergatologin.datos

import android.graphics.*
import kotlin.random.Random

/**
 * Interactive Captcha object.
 *
 * Contains the data about the image, the answer, a attempt counter and an indicator telling if the Captcha is still ready
 *
 * @author Gato
 */
class Captcha(){

    public lateinit var img: Bitmap
    private var ans: String = ""
    private var attempts: Int = 0
    public var isDead: Boolean = false

    constructor(w_px: Int, h_px: Int, att: Int, option: String, n1: Int = 0, n2: Int = 0) : this() {

        if(option.lowercase().equals("num")){
            numCaptcha(w_px, h_px, n1, n2)
        }else if(option.lowercase().equals("alphanum")){
            alphaNumCaptcha(w_px, h_px, n1)
        }else if (option.lowercase().equals("alpha")){
            alphaCaptcha(w_px, h_px, n1)
        }else if (option.trim().equals("")){
            throw Exception("The option parameter is not declared")
        }else{
            throw Exception("The option parameter is not recognized")
        }

        if(att > 0){
            this.attempts = att
        }else{
            throw Exception("The attempts parameter is zero or negative")
        }
    }

    fun checkAnswer(input: String): Boolean{
        if(!this.isDead){
            if(input.equals(this.ans)){
                this.isDead = true
                return true
            }else{
                this.attempts--
                if(this.attempts < 1){
                    this.isDead = true
                }
                return false
            }
        }else{
            throw Exception("The Captcha is not valid and doesn't accept any answering checks")
        }
    }

    private fun numCaptcha(w_px: Int, h_px: Int, n1: Int = 0, n2: Int = 0){

        var op1: Int = 0
        var op2: Int = 0
        lateinit var text: String
        val random = Random

        if (n1 == 0){
            op1 = (random.nextInt(10)+1)
        }else{
            op1 = n1
        }

        if (n2 == 0){
            op2 = (random.nextInt(10)+1)
        }else{
            op2 = n2
        }

        val opt = random.nextInt(2)

        if(opt == 0){
            this.ans = "${op1+op2}"
            text = "$op1 + $op2"
        }else{
            this.ans = "${op1*op2}"
            text = "$op1 x $op2"
        }
        generateImage(w_px, h_px, text)
    }

    private fun alphaNumCaptcha(w_px: Int, h_px: Int, len: Int){

        if(len < 1){
            throw Exception("The length for \"alphanum\" Captcha is zero or negative")
        }

        val random = Random

        var i = 0
        while(i < len){
            var chr = random.nextInt(123 - 49) + 49
            while((chr > 90 && chr < 97) ||
                (chr > 57 && chr < 65)) {
                chr = random.nextInt(123 - 49) + 49
            }
            this.ans += chr.toChar()
            i++
        }

        generateImage(w_px, h_px, this.ans)
    }

    private fun alphaCaptcha(w_px: Int, h_px: Int, len: Int){

        if(len < 1){
            throw Exception("The length for \"alpha\" Captcha is zero or negative")
        }

        val random = Random

        var i = 0
        while(i < len){
            var chr = random.nextInt(123 - 65) + 65
            while(chr > 90 && chr < 97)
                chr = random.nextInt(123 - 65) + 65
            this.ans += chr.toChar()
            i++
        }

        generateImage(w_px, h_px, this.ans)
    }

    private fun generateImage(w_px: Int, h_px: Int, text: String){

        val bitmap = Bitmap.createBitmap(w_px, h_px, Bitmap.Config.ARGB_8888)

        // Create a Canvas object with the Bitmap as its target
        val canvas = Canvas(bitmap)

        val c1 = color()
        val gradient: LinearGradient = LinearGradient(
            0F,
            0F,
            (w_px / text.length).toFloat(),
            (h_px / 2).toFloat(),
            c1,
            color(c1),
            Shader.TileMode.MIRROR
        )

        // Create a Paint object for the text
        val paint = Paint().apply {
            shader = gradient
            isDither = true
        }
        canvas.drawPaint(paint)

        // Draw the text at the center of the Bitmap
        val random = Random
        var x = 0F
        var i = 1
        while(i <= text.length){

            val paintText = Paint().apply {
                color = color()
                textSize = 80f
                isDither = true
            }

            val textBounds = Rect()
            paintText.getTextBounds(text, 0, text.length, textBounds)
            val increase = ((bitmap.width)/text.length)/2 + random.nextInt(text.length*2)
            x += increase

            val y = (bitmap.height + textBounds.height() + random.nextInt(text.length*2)+1) / 2f
            canvas.drawText(text.get(i-1).toString(), x, y, paintText)
            i++
        }

        val paintLine = Paint().apply {
            color = color()
            strokeWidth = (random.nextInt(5)+5).toFloat()
            isDither = true
        }
        val startX = random.nextInt(bitmap.width/4).toFloat()
        val startY = (bitmap.height/2)+random.nextInt(bitmap.height/4).toFloat()
        val stopX = bitmap.width-random.nextInt(bitmap.width/4).toFloat()
        val stopY = (bitmap.height)-random.nextInt(bitmap.height/4).toFloat()

        canvas.drawLine(startX, startY, stopX, stopY, paintLine)

        this.img = bitmap
    }
    private fun color(): Int {
        val list = listOf(Color.BLACK,
            Color.BLUE,
            Color.CYAN,
            Color.DKGRAY,
            Color.GRAY,
            Color.WHITE,
            Color.YELLOW,
            Color.RED,
            Color.MAGENTA,
            Color.GREEN)

        return list.random()
    }
    private fun color(c: Int): Int {
        val list = listOf(Color.BLACK,
            Color.BLUE,
            Color.CYAN,
            Color.DKGRAY,
            Color.GRAY,
            Color.WHITE,
            Color.YELLOW,
            Color.RED,
            Color.MAGENTA,
            Color.GREEN)

        var c2 = list.random()
        while(c2 == c)
            c2 = list.random()

        return c2
    }

    fun getAnswer() : String{
        return this.ans
    }
}