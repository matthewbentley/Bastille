import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aidan on 2/11/15.
 *
 * Scans the CWRU IP space, and does a brute-force resolve on all IPs
 * that are owned in our block
 */
public class Bastille {
    public static void main(String[] args){
        String[] macs = trimResults(makeIPs());
    }
    
    public static String[] formatMAC (String[] input){
        ArrayList<String> returnVar = new ArrayList<String>();
        
        for(String s: input){
            if(s.startsWith("tmp")){
                StringBuilder temp = new StringBuilder(s.substring(2));
                StringBuilder returnBuilder = new StringBuilder();
                String prefix = "";
                int index = 0;
                while (index < temp.length())
                {
                    returnBuilder.append(prefix);
                    prefix = ":";
                    returnBuilder.append(temp.substring(index,
                            Math.min(index + 2, temp.length())));
                    index += 2;
                }
                //TODO: check for a trailing addition
                returnVar.add(returnBuilder.toString());
            }
        }
        
        return returnVar.toArray(new String[returnVar.size()]);
    }

    /**
     * 
     * executes Soldier to get the hostnames
     * @return newline-split array of output
     */
    public static String[] makeIPs(){
        ArrayList<String> commands = new ArrayList<String>();
        commands.add("sudo");
        commands.add("sh");
        commands.add("./Soldier.sh");
        
        try {
            Process pr = Runtime.getRuntime().exec(commands.toArray(new String[commands.size()]));
            BufferedReader output = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String result;
            String totalResult = "";
            while((result = output.readLine()) != null){
                totalResult += result;
            }
            return totalResult.split("\n");
        } catch (IOException e) {
            System.err.println("ERROR: error while executing Soldier.");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * returns only the lines containing the desired tmp\W{12} hostname 
     * @param input output of makeIPs
     * @return strings that match the regex
     */
    public static String[] trimResults(String[] input){
        ArrayList<String> returnVar = new ArrayList<String>();
        Pattern pattern = Pattern.compile("(tmp\\W)");
        for(String s: input){
            //valid is tmp, followed by 12 non-whitespace characters
            Matcher matcher = pattern.matcher(s);
            if(matcher.find()) returnVar.add(matcher.group(1));
        }
        return returnVar.toArray(new String[returnVar.size()]);
    }
}
