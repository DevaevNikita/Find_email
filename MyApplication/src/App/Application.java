package App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {

    public static List<String> files = new LinkedList<>();

    public static void main(String[] args) {
        Object lock = new Object();
        List<String> emails = new ArrayList<>();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder text = new StringBuilder();
                try(BufferedReader in = new BufferedReader(new FileReader("test.txt"))) {
                    String s;
                    while ((s = in.readLine()) != null) {
                        text.append(s);
                        text.append("\n");
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
                Pattern email = Pattern.compile("(\\w+)(.*)?@(gmail|yandex|mail)\\.(com|ru)");
                Matcher matcher = email.matcher(text);
                synchronized (lock) {
                    while (matcher.find()) {
                        emails.add(matcher.group());
                    }
                }

            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder text = new StringBuilder();
                try(BufferedReader in = new BufferedReader(new FileReader("test2.txt"))){
                    String s;
                    while ((s = in.readLine()) != null){
                        text.append(s);
                        text.append("\n");
                    }
                }catch(IOException e){
                    System.out.println(e);
                }
                Pattern email = Pattern.compile("\\w+(.*)?@(mail|gmail|yandex)\\.(ru|com)");
                Matcher matcher = email.matcher(text);

                while (matcher.find()){
                    synchronized (lock){
                        emails.add(matcher.group());
                    }
                }
            }
        });

        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(emails);
    }
}
