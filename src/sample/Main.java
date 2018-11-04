package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import java.io.*;
import java.util.Scanner;


public class Main extends Application {

    PixelWriter pixelWriter;
    PixelWriter pixelWriter2;
    static double[][] matrixOfPoints;
    static boolean hasPoints = false;

    //Important 3D stuff
    int viewPortx = 6;
    int viewPorty = 8;
    double viewPortz = 7.5;
    int vSx = 100;//Vsx == Vsy == Vcx == Vcy
    int screenSize = 15;
    double designedView = 60;

    static boolean square = false;
    static boolean squarePyramid = false;
    static boolean tetrahedron = false;


    @Override
    public void start(Stage primaryStage) throws Exception{
        //prompt
        Scanner scan = new Scanner(System.in);
        int numOfPoints;
        if(!hasPoints) {
            System.out.println("How many points do you want to draw?");
            numOfPoints = scan.nextInt();

            matrixOfPoints = new double[numOfPoints][4];

            for (int row = 0; row < numOfPoints; row++) {
                for (int col = 0; col < 4; col++) {
                    if (col == 0) {
                        System.out.print("x" + row + ": ");
                        int x = scan.nextInt();                 //  _             _
                        matrixOfPoints[row][col] = x;           // |  x1   y1   1  |
                        //scan.close();                         // |  x2   y2   1  |
                    }                                           //  -             -
                    else if (col == 1) {
                        System.out.print("y" + row + ": ");
                        int y = scan.nextInt();
                        matrixOfPoints[row][col] = y;
                    }
                    else if (col == 2) {
                        System.out.print("z" + row + ": ");
                        int z = scan.nextInt();
                        matrixOfPoints[row][col] = z;
                    }
                    else {
                        matrixOfPoints[row][col] = 1;
                    }
                }
            }
        }
        numOfPoints = matrixOfPoints.length;

        double[][] twoDMatrix = new double[numOfPoints][2];


        for(int row = 0;row<matrixOfPoints.length;row++){
            for(int col = 0;col<2;col++){
                twoDMatrix[row][col] = 0;
            }
        }

        //This is where I need to convert the Vsx and Vsy stuff
        //convert3dto2d(matrixOfPoints, twoDMatrix);
        double[][] VbyN = new double[4][4];
        VbyN = calculateVbyN();
        twoDMatrix = performPerspective(VbyN, matrixOfPoints);
        convert3dto2d(twoDMatrix, twoDMatrix);

        //copy to show original with button
        double[][] originalMatrix = new double[twoDMatrix.length][twoDMatrix[0].length];
        for(int row = 0;row<twoDMatrix.length;row++){
            for(int col = 0;col<twoDMatrix[0].length;col++){
                originalMatrix[row][col] = twoDMatrix[row][col];
            }
        }




        char assignment;
        boolean flag = true;

        while (flag == true) {

            System.out.println("Operations: Translate(t), Scale(s), Rotate(r), Quit(q), Exit(e)");
            assignment = scan.next().charAt(0);

            switch (assignment) {
                case 't':
                    System.out.print("Tx: ");
                    int Tx = scan.nextInt();
                    System.out.print("Ty: ");
                    int Ty = scan.nextInt();
                    System.out.print("Tz: ");
                    int Tz = scan.nextInt();
                    BasicTranslate(Tx, Ty, Tz, matrixOfPoints);
                    twoDMatrix = performPerspective(VbyN, matrixOfPoints);
                    convert3dto2d(twoDMatrix, twoDMatrix);
                    break;

                case 's':
                    System.out.print("Sx: ");
                    int Sx = scan.nextInt();
                    System.out.print("Sy: ");
                    int Sy = scan.nextInt();
                    System.out.print("Sz: ");
                    int Sz = scan.nextInt();
                    System.out.print("Cx: ");
                    int Cx = scan.nextInt();
                    System.out.print("Cy: ");
                    int Cy = scan.nextInt();
                    System.out.print("Cz: ");
                    int Cz = scan.nextInt();
                    Scale(Sx, Sy, Sz, Cx, Cy, Cz, matrixOfPoints);
                    twoDMatrix = performPerspective(VbyN, matrixOfPoints);
                    convert3dto2d(twoDMatrix, twoDMatrix);
                    break;

                case 'r':
                    System.out.print("Theta: ");
                    double theta = scan.nextInt();
                    theta = Math.toRadians(theta);
                    System.out.print("Cx: ");
                    Cx = scan.nextInt();
                    System.out.print("Cy: ");
                    Cy = scan.nextInt();
                    System.out.print("Cz: ");
                    Cz = scan.nextInt();
                    Rotate(theta, Cx, Cy, Cz, matrixOfPoints);
                    twoDMatrix = performPerspective(VbyN, matrixOfPoints);
                    convert3dto2d(twoDMatrix, twoDMatrix);
                    break;

                case 'q':
                    flag = false;
                    break;

                case 'e':
                    System.exit(0);

            }

        }

        System.out.println("Would you like to output the files? (y/n)");
        char answer = scan.next().charAt(0);
        if (answer == 'y') {
            System.out.println("name of file for output");
            String fileName = scan.next();
            File file = new File(System.getProperty("user.dir") + "/src/sample/" + fileName);
            OutputLines(file, numOfPoints);
        }

        Button btn = new Button();
        btn.setText("Show Original");
        btn.setOnAction((ActionEvent event) -> {
            Group root2 = new Group();
            Scene secondScene = new Scene(root2, 1024, 768, Color.WHITE);

            final Canvas canvas2 = new Canvas(1024, 768);

            pixelWriter2 = canvas2.getGraphicsContext2D().getPixelWriter();
            DisplayPixels(originalMatrix, 12, pixelWriter2);


            root2.getChildren().add(canvas2);

            Stage secondStage = new Stage();
            secondStage.setTitle("Original Polygon");
            secondStage.setScene(secondScene);

            secondStage.show();
        });



        Group root = new Group();
        Scene s = new Scene(root, 1024, 768, Color.WHITE);
        final Canvas canvas = new Canvas(1024, 768);

        pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        DisplayPixels(twoDMatrix, numOfPoints, pixelWriter);

        root.getChildren().add(canvas);
        root.getChildren().add(btn);
        primaryStage.setTitle("Homework 2: CSCI 4810");
        primaryStage.setScene(s);
        primaryStage.show();


    }


    private double[][] performPerspective(double[][] H, double[][] matrix) {//H = V*N
        double[][] newMatrix = new double[matrix.length][4];

        for (int iterator = 0; iterator < matrix.length; iterator++) {
            //int iterator = 0
            //make 1x3 matrix from matrixOfPoints
            double[][] testMatrix = new double[1][4];
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 4; col++) {
                    if (col == 3)
                        testMatrix[0][col] = 1;
                    else
                        testMatrix[0][col] = matrix[row][col];
                }
            }

            testMatrix = multiplyMatrices(testMatrix, H, testMatrix.length, testMatrix[0].length, H[0].length);

            //inserting back into matrixOfPoints
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 4; col++) {
                    if(col ==3){
                        newMatrix[row][col] = 1;
                    }
                    else
                        newMatrix[row][col] = testMatrix[0][col];
                }
            }
        }


        return newMatrix;
    }

    private double[][] calculateVbyN() {
        double[][] VbyN = new double[4][4];
        double costhetaT3 = viewPorty/(Math.sqrt(Math.pow(viewPortx,2)+Math.pow(viewPorty,2)));
        double sinthetaT3 = viewPortx/(Math.sqrt(Math.pow(viewPortx,2)+Math.pow(viewPorty,2)));
        double costhetaT4 = (Math.sqrt(Math.pow(viewPortx,2)+Math.pow(viewPorty,2)))/Math.sqrt(Math.pow(viewPortz,2)+(Math.pow((Math.sqrt(Math.pow(viewPortx,2)+Math.pow(viewPorty,2))),2)));
        double sinthetaT4 = viewPortz/Math.sqrt(Math.pow(viewPortz,2)+Math.pow((Math.sqrt(Math.pow(viewPortx,2)+Math.pow(viewPorty,2))),2));

        //STEP 1: MAKE T1
        double[][] T1 = new double[4][4];


        for(int row = 0;row<4;row++){
            for(int col = 0;col<4;col++){
                if(row==col){
                    T1[row][col] = 1;
                }
                else if(row ==3){
                    if(col == 0)
                        T1[row][col] = -viewPortx;
                    else if(col == 1)
                        T1[row][col] = -viewPorty;
                    else if(col ==2)
                        T1[row][col] = -viewPortz;
                    else
                        T1[row][col] = 0;
                }
                else {
                    T1[row][col] = 0;
                }
            }
        }

        //STEP 2: MAKE T2
        double[][] T2 = new double[4][4];


        for(int row = 0;row<4;row++){
            for(int col = 0;col<4;col++){
                if((row==0 && col==0)||(row==3&&col==3)||(row==2 && col==1))
                    T2[row][col] = 1;
                else if(row==1 && col == 2)
                    T2[row][col] = -1;
                else
                    T2[row][col] = 0;
            }
        }

        //STEP 3: MAKE T3
        double[][] T3 = new double[4][4];


        for(int row = 0;row<4;row++){
            for(int col = 0;col<4;col++){
                if((row==0 && col==0)||(row==2&&col==2))
                    T3[row][col] = -costhetaT3;
                else if(row == 2 && col == 0)
                    T3[row][col] = -sinthetaT3;
                else if(row ==0 && col ==2)
                    T3[row][col] = sinthetaT3;
                else if((row ==1 && col == 1)||(row ==3&&col == 3))
                    T3[row][col] = 1;
                else
                    T3[row][col] = 0;
            }
        }

        //STEP 4: MAKE T4
        double[][] T4 = new double[4][4];


        for(int row = 0;row<4;row++){
            for(int col = 0;col<4;col++){
                if((row==1 && col==1)||(row==2&&col==2))
                    T4[row][col] = costhetaT4;
                else if(row==1 && col == 2)
                    T4[row][col] = sinthetaT4;
                else if(row==2 && col ==1)
                    T4[row][col] = -sinthetaT4;
                else if((row ==0 && col == 0)||(row ==3&&col == 3))
                    T4[row][col] = 1;
                else
                    T4[row][col] = 0;
            }
        }

        //STEP 5: MAKE T5
        double[][] T5 = new double[4][4];


        for(int row = 0;row<4;row++){
            for(int col = 0;col<4;col++){
                if (row==2 && col==2)
                    T5[row][col] = -1;
                else if(row==col)
                    T5[row][col] = 1;
                else
                    T5[row][col] = 0;
            }
        }


        //STEP 6: MULTIPLY TOGETHER AND STORE IN V
        VbyN = multiplyMatrices(T1,T2, T1.length, T1[0].length, T2[0].length);
        VbyN = multiplyMatrices(VbyN,T3,VbyN.length,VbyN[0].length,T3[0].length);
        VbyN = multiplyMatrices(VbyN,T4, VbyN.length,VbyN[0].length,T4[0].length);
        VbyN = multiplyMatrices(VbyN,T5, VbyN.length,VbyN[0].length,T5[0].length);


        double[][] N = new double[4][4];

        for(int row = 0;row<4;row++){
            for(int col = 0;col<4;col++){
                if((row==0 && col==0)||(row==1 && col ==1))
                    N[row][col] = designedView/screenSize;//designed/screen
                else if((row == 2 && col ==2)||(row==3 && col==3))
                    N[row][col] = 1;
                else
                    N[row][col] = 0;
            }
        }

        VbyN = multiplyMatrices(VbyN,N, VbyN.length, VbyN[0].length, N[0].length);
        return VbyN;
        
    }

    //https://www.programiz.com/java-programming/examples/multiply-matrix-function w/ a little bit of change
    public static double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix, int r1, int c1, int c2) {
        double[][] product = new double[r1][c2];
        for(int i = 0; i < r1; i++) {
            for (int j = 0; j < c2; j++) {
                for (int k = 0; k < c1; k++) {
                    product[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
                }
            }
        }

        return product;
    }

    private void convert3dto2d(double[][] matrixOfPoints, double[][] twoDMatrix) {
        double xS;
        double yS;

        for (int iterator = 0; iterator<matrixOfPoints.length;iterator++) {


            double[][] pointsToConvert = new double[1][3];
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 3; col++) {
                    pointsToConvert[0][col] = matrixOfPoints[row][col];
                }
            }
            double xE = pointsToConvert[0][0];
            double yE = pointsToConvert[0][1];
            double zE = pointsToConvert[0][2];

            xS = (xE/zE)*vSx+vSx;
            yS = (yE/zE)*vSx+vSx;

            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 2; col++) {
                    if(col == 0){
                        twoDMatrix[row][col] = xS;
                    }
                    if(col ==1){
                        twoDMatrix[row][col] = yS;
                    }
                }
            }
        }
    }

    public double[][] BasicTranslate(int Tx, int Ty, int Tz, double[][] matrix){

        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[0].length;j++){
                if(j==0){
                    matrix[i][j] += Tx;
                }
                else if(j==1){
                    matrix[i][j] += Ty;
                }
                else if(j==2){
                    matrix[i][j] += Tz;
                }
                else {
                    matrix[i][j] = 1;
                }
            }
        }

        /*for (int iterator = 0; iterator < matrix.length; iterator++) {
            //int iterator = 0
            //make 1x3 matrix from matrixOfPoints
            double[][] newMatrix = new double[1][4];
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 4; col++) {
                    newMatrix[0][col] = matrix[row][col];
                }
            }

            //Make Translate Matrix
            double[][] translateMatrix = new double[4][4];
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    if (row == col) {
                        translateMatrix[row][col] = 1;
                    } else if (row == 3) {
                        if (col == 0)
                            translateMatrix[row][col] = Tx;
                        else if (col == 1)
                            translateMatrix[row][col] = Ty;
                        else if (col == 2)
                            translateMatrix[row][col] = Tz;
                        else
                            translateMatrix[row][col] = 0;
                    } else {
                        translateMatrix[row][col] = 0;
                    }
                }
            }

            newMatrix = multiplyMatrices(newMatrix, translateMatrix, 1, 4, 4);

            //inserting back into matrixOfPoints
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 4; col++) {
                    matrix[row][col] = newMatrix[0][col];
                }
            }

        }*/
        return matrix;

    }

    public double[][] BasicScale(int Sx, int Sy, int Sz, double[][] matrix){
        for(int row=0;row<matrix.length;row++){
            for(int col = 0;col<matrix[0].length;col++){
                if(col==0){
                    matrix[row][col] *= Sx;
                }
                if(col==1){
                    matrix[row][col] *= Sy;
                }
                if(col==2){
                    matrix[row][col] *= Sz;
                }
            }
        }
        return matrix;
    }

    public double[][] BasicRotate(double angle, double[][] matrix){
        for(int row=0;row<matrix.length;row++){
            double x = matrix[row][0];
            double y = matrix[row][1];
            double z = matrix[row][2];
            for(int col = 0;col<matrix[0].length;col++){
                if(col==0){
                    matrix[row][col] = x*Math.cos(angle) + y*Math.sin(angle);
                }
                if(col==1){
                    matrix[row][col] = x*-Math.sin(angle) + y*Math.cos(angle);
                }
            }
        }
        return matrix;
    }

    public void Scale(int Sx, int Sy, int Sz, int Cx, int Cy, int Cz, double[][] matrix) {

        for (int iterator = 0; iterator < matrix.length; iterator++){
            //int iterator = 0
            //make 1x3 matrix from matrixOfPoints
            double[][] newMatrix = new double[1][4];
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 4; col++) {
                    newMatrix[0][col] = matrix[row][col];
                }
            }

            newMatrix = BasicTranslate(-Cx, -Cy, -Cz, newMatrix);
            newMatrix = BasicScale(Sx, Sy, Sz, newMatrix);
            newMatrix = BasicTranslate(Cx, Cy, Cz, newMatrix);

            //inserting back into matrixOfPoints
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 4; col++) {
                    matrix[row][col] = newMatrix[0][col];
                }
            }
        }

    }

    public void Rotate(double angle, int Cx, int Cy, int Cz, double[][] matrix){

        //making and multiplying matrices
        for (int iterator = 0; iterator < matrix.length; iterator++){
            //int iterator = 0
            //make 1x3 matrix from matrixOfPoints
            double[][] newMatrix = new double[1][4];
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 4; col++) {
                    newMatrix[0][col] = matrix[row][col];
                }
            }


            newMatrix = BasicTranslate(-Cx, -Cy, -Cz, newMatrix);
            newMatrix = BasicRotate(angle, newMatrix);
            newMatrix = BasicTranslate(Cx, Cy, Cz, newMatrix);


            //inserting back into matrixOfPoints
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 4; col++) {
                    matrix[row][col] = newMatrix[0][col];
                }
            }
        }
    }

    public void DisplayPixels(double[][] datalines, int num, PixelWriter pw){


        if(square) {
            BresenhamAlg((int) datalines[0][0], (int) datalines[0][1], (int) datalines[1][0], (int) datalines[1][1], pw);//AB
            BresenhamAlg((int) datalines[1][0], (int) datalines[1][1], (int) datalines[2][0], (int) datalines[2][1], pw);//BC
            BresenhamAlg((int) datalines[2][0], (int) datalines[2][1], (int) datalines[3][0], (int) datalines[3][1], pw);//CD
            BresenhamAlg((int) datalines[3][0], (int) datalines[3][1], (int) datalines[0][0], (int) datalines[0][1], pw);//DA
            BresenhamAlg((int) datalines[4][0], (int) datalines[4][1], (int) datalines[5][0], (int) datalines[5][1], pw);//EF
            BresenhamAlg((int) datalines[5][0], (int) datalines[5][1], (int) datalines[6][0], (int) datalines[6][1], pw);//FG
            BresenhamAlg((int) datalines[6][0], (int) datalines[6][1], (int) datalines[7][0], (int) datalines[7][1], pw);//GH
            BresenhamAlg((int) datalines[7][0], (int) datalines[7][1], (int) datalines[4][0], (int) datalines[4][1], pw);//HE
            BresenhamAlg((int) datalines[0][0], (int) datalines[0][1], (int) datalines[4][0], (int) datalines[4][1], pw);//AE
            BresenhamAlg((int) datalines[1][0], (int) datalines[1][1], (int) datalines[5][0], (int) datalines[5][1], pw);//BF
            BresenhamAlg((int) datalines[2][0], (int) datalines[2][1], (int) datalines[6][0], (int) datalines[6][1], pw);//CG
            BresenhamAlg((int) datalines[3][0], (int) datalines[3][1], (int) datalines[7][0], (int) datalines[7][1], pw);//DH
        }
        else if(squarePyramid){
            BresenhamAlg((int) datalines[0][0], (int) datalines[0][1], (int) datalines[1][0], (int) datalines[1][1], pw);//AB
            BresenhamAlg((int) datalines[2][0], (int) datalines[2][1], (int) datalines[3][0], (int) datalines[3][1], pw);//CD
            BresenhamAlg((int) datalines[0][0], (int) datalines[0][1], (int) datalines[2][0], (int) datalines[2][1], pw);//AC
            BresenhamAlg((int) datalines[1][0], (int) datalines[1][1], (int) datalines[3][0], (int) datalines[3][1], pw);//BD
            BresenhamAlg((int) datalines[0][0], (int) datalines[0][1], (int) datalines[4][0], (int) datalines[4][1], pw);//AE
            BresenhamAlg((int) datalines[1][0], (int) datalines[1][1], (int) datalines[4][0], (int) datalines[4][1], pw);//BE
            BresenhamAlg((int) datalines[2][0], (int) datalines[2][1], (int) datalines[4][0], (int) datalines[4][1], pw);//CE
            BresenhamAlg((int) datalines[3][0], (int) datalines[3][1], (int) datalines[4][0], (int) datalines[4][1], pw);//CE
        }
        else if(tetrahedron){
            BresenhamAlg((int) datalines[0][0], (int) datalines[0][1], (int) datalines[1][0], (int) datalines[1][1], pw);//AB
            BresenhamAlg((int) datalines[1][0], (int) datalines[1][1], (int) datalines[2][0], (int) datalines[2][1], pw);//BC
            BresenhamAlg((int) datalines[0][0], (int) datalines[0][1], (int) datalines[2][0], (int) datalines[2][1], pw);//AC
            BresenhamAlg((int) datalines[2][0], (int) datalines[2][1], (int) datalines[3][0], (int) datalines[3][1], pw);//CD
            BresenhamAlg((int) datalines[0][0], (int) datalines[0][1], (int) datalines[3][0], (int) datalines[3][1], pw);//AD
            BresenhamAlg((int) datalines[1][0], (int) datalines[1][1], (int) datalines[3][0], (int) datalines[3][1], pw);//BD
        }
        else{
            for(int i=0;i<datalines.length;i++){
                if(i+1 != datalines.length){
                    BresenhamAlg((int)datalines[i][0], (int)datalines[i][1], (int)datalines[i+1][0], (int)datalines[i+1][1], pw);
                }
                if(i==datalines.length-1){
                    BresenhamAlg((int)datalines[i][0], (int)datalines[i][1], (int)datalines[0][0], (int)datalines[0][1], pw);
                }

            }
        }



    }

    public static int InputLines(File datalines, int num) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(datalines));
        String string;
        int numOfRows=0;

        while ((string = br.readLine())!= null){
            numOfRows++;
            System.out.println(string);
        }
        matrixOfPoints = new double[numOfRows][4];


        BufferedReader br2 = new BufferedReader(new FileReader(datalines));
        int row = 0;

        while ((string = br2.readLine())!= null){
            String firstNum = string.substring(0,string.indexOf(' '));
            double valuex = Double.parseDouble(firstNum);
            string = string.substring(string.indexOf(' ')+1);

            String secondNum = string.substring(0, string.indexOf(' '));
            double valuey = Double.parseDouble(secondNum);
            string = string.substring(string.indexOf(' ')+1);

            String thirdNum = string.substring(0, string.length());
            double valuez = Double.parseDouble(thirdNum);

            //System.out.println(valuex + " " + valuey);
            matrixOfPoints[row][0] = valuex;
            matrixOfPoints[row][1] = valuey;
            matrixOfPoints[row][2] = valuez;
            matrixOfPoints[row][3] = 1;
            row++;

        }
        hasPoints = true;

        return numOfRows;
    }

    public void OutputLines(File datalines, int num) throws IOException {
        //File newFile = new File(String.valueOf(datalines));
        if(datalines.createNewFile()){
            System.out.println("file created");
        }
        else{
            System.out.println("file already exists");
        }
        FileWriter writer = new FileWriter(datalines);

        //writer.write("whatever");
        for(int row=0;row<matrixOfPoints.length;row++){
            for(int col=0;col<matrixOfPoints[0].length-1;col++){
                writer.write(String.valueOf(matrixOfPoints[row][col])+" ");
                //System.out.print(matrixOfPoints[row][col] + " ");
                if(col ==matrixOfPoints[0].length-2){
                    writer.write("\n");
                }
            }
        }
        writer.close();




    }

    public static void printMatrix(double[][] mat){
        //Test printing out matrixOfPoints
        for(int row=0;row<mat.length;row++){
            for(int col=0;col<2;col++){
                System.out.print(matrixOfPoints[row][col] + " ");
                if(col ==1){
                    System.out.println();
                }
            }
        }
    }

    public void BresenhamAlg(int x0, int y0, int x1, int y1, PixelWriter pw){
        //straight from https://en.wikipedia.org/wiki/Bresenham's_line_algorithm
        //and https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Java
        int deltax = Math.abs(x1-x0);
        int deltay = Math.abs(y1-y0);
        int tempx = x0;
        int tempy = y0;
        int xincrement = x0<x1 ? 1 : -1;
        int yincrement = y0<y1 ? 1 : -1;
        int e=0;
        int deltax2 = 2*deltax;
        int deltay2 = 2*deltay;

        //vertical line
        if(deltax==0){
            for(int i=y0;i<y1;i++){
                pw.setColor(tempx,i,Color.BLUE);
            }
            for(int i=y0;i>y1;i--){
                pw.setColor(tempx,i,Color.BLUE);
            }
            return;
        }
        //horizontal line
        if (deltay == 0) {
            for (int i=x0;i<x1;i++){
                pw.setColor(i,tempy,Color.BLUE);
            }
            for (int i=x0;i>x1;i--){
                pw.setColor(i,tempy,Color.BLUE);
            }
            return;
        }

        if(deltax >= deltay) {
            while(true) {
                pw.setColor(tempx, tempy, Color.BLUE);
                if(tempx==x1)
                    break;
                tempx += xincrement;
                e += deltay2;
                if(e > deltax) {
                    tempy += yincrement;
                    e -= deltax2;
                }

            }
        }else {
            while(true) {
                pw.setColor(tempx,tempy, Color.BLUE);
                if(tempy == y1)
                    break;
                tempy += yincrement;
                e += deltax2;
                if(e> deltay) {
                    tempx += xincrement;
                    e -= deltay2;
                }
            }
        }

    }


    public static void main(String[] args) throws FileNotFoundException {
        if(args.length >0) {
            String argName = args[0].toString();

            if(argName.equals("givenSquare.txt")){
                square = true;
            }
            else if(argName.equals("squarePryamid.txt")){
                squarePyramid = true;
            }
            else if(argName.equals("tetrahedron.txt")){
                tetrahedron = true;
            }

            File file = new File(System.getProperty("user.dir")+"/src/sample/" + args[0]);
            try {
                InputLines(file, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        launch(args);
    }
}
