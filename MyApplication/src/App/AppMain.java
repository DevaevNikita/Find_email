package App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppMain {

    public static List<String> files = new LinkedList<>();
    public static List<String> emails = new ArrayList<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {


        Scanner scanner = new Scanner(System.in);
        System.out.printf("Введите колиство файолв: ");
        Integer n = scanner.nextInt();
        scanner.nextLine();

        ExecutorService es = Executors.newFixedThreadPool(n);

        for(int i = 0; i < n; i++){
            System.out.printf("Введите название " + (i+1) + "-ого файла с раширением: ");
            String filename = scanner.nextLine();
            AppMain.files.add(filename);
        }
            for(int i = 0; i < n; i++) {
                String file_name = AppMain.files.get(i);
                es.submit(new Runnable() {
                    @Override
                    public void run() {
                        AppMain.find_email(file_name);
                    }
                });

        }
        es.shutdown();
        es.awaitTermination(1, TimeUnit.DAYS);

        System.out.println(emails);
    }

    public static void find_email(String file_name){
        Object lock = new Object();
        String string = file_name;
        StringBuilder text = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new FileReader(string));) {
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
                AppMain.emails.add(matcher.group());
            }
        }
    }

}

