import sun.util.resources.fr.CurrencyNames_fr_FR

/*
fun main (args: Array<String>){
// déclaration d'une constante
val nom:String ="Dupont" // déclaration d'une variable altérable
var prenom ="Marcel"
    var age =34
    age++

println("Bonjour$prenom $nom, vous avez $age ans")


        val (a:Any, b:Any) = (readLine()?:"nil null"?.toString())?.split(' ');
        println(a + b )


}

private infix fun Int.age(argvalue: Int): Int {
    println( " accessor : $argvalue")
 return argvalue
}
 */
open class MyClass {


    var moneyten = mutableMapOf<String, Array<String>>(
        "*" to  arrayOf("un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf"),
        "--" to arrayOf("dix", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante-*dix", "quatre-vingt", "quatre-vingt-*dix"),
        "---" to arrayOf("cent"),
        "----" to arrayOf("mille"),
        "dix" to arrayOf("onze","douze","treize", "quatorze", "quinze", "seize"),
        "vingt" to arrayOf("et un"),
        "soixante-*dix" to arrayOf("*dix")
    )

    fun convert(argStr: String) : String {
        var integerpart = ""
        var floatpart = ""
        var argValStr = argStr?:"0.0".trim();
        println(" inarg part "+(argStr?:"") +" ++ " +argValStr)
        when{
            argValStr.length >0 ->{

                when {
                    ( argValStr.indexOf(".") != -1) ->{
                        integerpart = argValStr.substring(0, argValStr.indexOf("."));
                        floatpart = argValStr.substring(argValStr.indexOf(".")+1, argValStr.length);
                    }
                    ( argValStr.indexOf(",") != -1) ->{

                        integerpart = argValStr.substring(0, argValStr.indexOf(","));
                        floatpart = argValStr.substring(argValStr.indexOf(",")+1, argValStr.length);

                    }
                    else ->{
                        integerpart = argValStr;
                    }
                }
            }
        }
        /* ********************************************* */
        println(" integer part "+integerpart)
        println(" float part "+floatpart)
        when{
            integerpart.length >0 -> {
                println(" integer part "+moneyten.keys.elementAt(integerpart.length))
                println(" integer part key "+ moneyten[moneyten.keys.elementAt(integerpart.length-1)]?.elementAt(integerpart.substring(0,1).toInt()))
                println(" integer part key "+ moneyten[moneyten.keys.elementAt(0)]?.elementAt(integerpart.substring(1,2).toInt()))
            }
        }
        return "[NULL]"
    }

    fun helloWorld(astr: String = "World"): String {
       when {
           astr?.trim().length >0 -> return " Hello, "+(astr)+"!"
           else -> return "[NULL]";
       }
        return "";
    }



}