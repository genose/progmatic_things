public class Decipherer {

    public static String message1 = "0@sn9sirppa@#?ia'jgtvryko1";
    public static String message2 = "q8e?wsellecif@#?sel@#?setuotpazdsy0*b9+mw@x1vj";
    public static String message3 = "aopi?sedohtém@#?sedhtmg+p9l!";


    public static String DeciphererMessage (String messageCodee){
        String messageToDecode = messageCodee;
        int messageLength = messageToDecode.length();


        messageToDecode = messageToDecode.substring(5, (messageLength / 2) + 5);

        messageToDecode = messageToDecode.replaceAll("@#\\?", " ");

        return  (new StringBuilder( messageToDecode)).reverse().toString();


    }

}
