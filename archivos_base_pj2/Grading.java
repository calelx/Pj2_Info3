import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public class Grading {
    private static final String AFN_PATH = "./tests/afn/";
    private static final String STRINGS_PATH = "./tests/strings/";
    private static final int STRING_INDEX = 0;
    private static final int ACCEPTANCE_INDEX = 1;
    
    public static void main(String[] args) throws Exception {
        String[] filesList = getFilesList();
        double finalScore = iterateTests(filesList);
        displayFinalScore(finalScore);
    }

    private static String[] getFilesList(){
        File folder = new File(AFN_PATH);
        File[] filesList = folder.listFiles();
        String[] fileNames = new String[filesList.length];
        for(int i = 0; i < filesList.length; i++){
            fileNames[i] = filesList[i].getName();
        }

        return fileNames;
    }

    private static double iterateTests(String[] filesList) throws Exception{
        double finalScore = 0;
        double testScore = 100.0 / filesList.length;

        for(int i = 0; i < filesList.length; i++){
            String fileName = filesList[i];
            if(testSuccessful(fileName, testScore)){
                finalScore += testScore;
            }
        }

        return finalScore;
    }

    private static boolean testSuccessful(String fileName, double testScore) throws Exception{
        String afnPath = getAfnPath(fileName);
        String stringsPath = getStringsPath(fileName);

        System.out.println("\nVerificando Test.................................." + afnPath);

        AFN afn = new AFN(afnPath);
        BufferedReader reader = new BufferedReader(new FileReader(stringsPath));
        String line = reader.readLine();
        
        Boolean successful = true;
        while(line != null && line.length() > 0){
            String[] testData = line.split(" ");
            boolean obtained = afn.accept(testData[STRING_INDEX]);
            String expected = testData[ACCEPTANCE_INDEX];

            if(!obtained && expected.equals("T")){
                successful = false;
            }
            
            if(obtained && expected.equals("F")){
                successful = false;
            }

            

            String failedTestString = "";
            if (! successful) {
                failedTestString = " - TEST FALLIDO (+0)";
            }
            System.out.println("Obtenido: " + obtained + " - Esperado: " + expected + failedTestString );
            if (! successful){
                reader.close();
                return false;
            }
            line = reader.readLine();
        }
        String scoreString = String.format("%3d", Math.round(testScore));
        System.out.println("TEST APROBADO ............................................................................... +" + scoreString);
        reader.close();
        return successful;
    }

    private static String getAfnPath(String afnFileName){
        return AFN_PATH + afnFileName;
    }

    private static String getStringsPath(String afnFileName){
        String fileName = afnFileName.split("\\.")[0];
        return STRINGS_PATH + fileName + ".txt";
    }

    private static void displayFinalScore(double finalScore){
        String scoreString = String.format("%3d", Math.round(finalScore));
        System.out.println("Puntuacion Final............................................................................" + scoreString + "/100");
    }
}
