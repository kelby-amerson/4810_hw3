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
    double[][] concatMatrix;
    static boolean hasPoints = false;

    //Important 3D stuff
    int viewPortx = 6;
    int viewPorty = 8;
    double viewPortz = 7.5;
    int screenSize = 30;
    int designedView = 60;



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

        double[][] originalMatrix = new double[numOfPoints][3];

        for(int row = 0;row<matrixOfPoints.length;row++){
            for(int col = 0;col<matrixOfPoints[0].length;col++){
                originalMatrix[row][col] = matrixOfPoints[row][col];
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
                    BasicTranslate(Tx, Ty, matrixOfPoints);
                    break;

                case 's':
                    System.out.print("Sx: ");
                    int Sx = scan.nextInt();
                    System.out.print("Sy: ");
                    int Sy = scan.nextInt();
                    System.out.print("Cx: ");
                    int Cx = scan.nextInt();
                    System.out.print("Cy: ");
                    int Cy = scan.nextInt();
                    Scale(Sx, Sy, Cx, Cy, matrixOfPoints);
                    break;

                case 'r':
                    System.out.print("Theta: ");
                    double theta = scan.nextInt();
                    theta = Math.toRadians(theta);
                    System.out.print("Cx: ");
                    Cx = scan.nextInt();
                    System.out.print("Cy: ");
                    Cy = scan.nextInt();
                    Rotate(theta, Cx, Cy, matrixOfPoints);
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
        DisplayPixels(matrixOfPoints, numOfPoints, pixelWriter);

        root.getChildren().add(canvas);
        root.getChildren().add(btn);
        primaryStage.setTitle("Homework 2: CSCI 4810");
        primaryStage.setScene(s);
        primaryStage.show();


    }

    public double[][] BasicTranslate (int Tx, int Ty, double[][] matrix){
        for(int row=0;row<matrix.length;row++){
            for(int col = 0;col<matrix[0].length;col++){
                if(col==0){
                    matrix[row][col] += Tx;
                }
                if(col==1){
                    matrix[row][col] += Ty;
                }
            }
        }
        return matrix;
    }

    public double[][] BasicScale(int Sx, int Sy, double[][] matrix){
        for(int row=0;row<matrix.length;row++){
            for(int col = 0;col<matrix[0].length;col++){
                if(col==0){
                    matrix[row][col] *= Sx;
                }
                if(col==1){
                    matrix[row][col] *= Sy;
                }
            }
        }
        return matrix;
    }

    public double[][] BasicRotate(double angle, double[][] matrix){
        for(int row=0;row<matrix.length;row++){
            double x = matrix[row][0];
            double y = matrix[row][1];
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

    public void Scale(int Sx, int Sy, int Cx, int Cy, double[][] matrix) {

        for (int iterator = 0; iterator < matrix.length; iterator++){
            //int iterator = 0
            //make 1x3 matrix from matrixOfPoints
            double[][] newMatrix = new double[1][3];
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 2; col++) {
                    newMatrix[0][col] = matrix[row][col];
                }
            }

            newMatrix = BasicTranslate(-Cx, -Cy, newMatrix);
            newMatrix = BasicScale(Sx, Sy, newMatrix);
            newMatrix = BasicTranslate(Cx, Cy, newMatrix);

            //inserting back into matrixOfPoints
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 2; col++) {
                    matrix[row][col] = newMatrix[0][col];
                }
            }
        }

    }

    public void Rotate(double angle, int Cx, int Cy, double[][] matrix){

        //making and multiplying matrices
        for (int iterator = 0; iterator < matrix.length; iterator++){
            //int iterator = 0
            //make 1x3 matrix from matrixOfPoints
            double[][] newMatrix = new double[1][3];
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 2; col++) {
                    newMatrix[0][col] = matrix[row][col];
                }
            }


            newMatrix = BasicTranslate(-Cx, -Cy, newMatrix);
            newMatrix = BasicRotate(angle, newMatrix);
            newMatrix = BasicTranslate(Cx, Cy, newMatrix);


            //inserting back into matrixOfPoints
            for (int row = iterator; row < iterator + 1; row++) {
                for (int col = 0; col < 2; col++) {
                    matrix[row][col] = newMatrix[0][col];
                }
            }
        }
    }

    public void DisplayPixels(double[][] datalines, int num, PixelWriter pw){

        for(int i=0;i<datalines.length;i++){
            if(i+1 != datalines.length){
                BresenhamAlg((int)datalines[i][0], (int)datalines[i][1], (int)datalines[i+1][0], (int)datalines[i+1][1], pw);
            }
            if(i==datalines.length-1){
                BresenhamAlg((int)datalines[i][0], (int)datalines[i][1], (int)datalines[0][0], (int)datalines[0][1], pw);
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
        matrixOfPoints = new double[numOfRows][3];


        BufferedReader br2 = new BufferedReader(new FileReader(datalines));
        int row = 0;

        while ((string = br2.readLine())!= null){
            String firstNum = string.substring(0,string.indexOf(' '));
            int valuex = Integer.parseInt(firstNum);
            String secondNum = string.substring(string.indexOf(' ')+1, string.indexOf(' '));
            int valuey = Integer.parseInt(secondNum);
            String thirdNum = string.substring(string.lastIndexOf(' ')+1);
            int valuez = Integer.parseInt(thirdNum);

            System.out.println(valuex + " " + valuey);
            matrixOfPoints[row][0] = valuex;
            matrixOfPoints[row][1] = valuey;
            matrixOfPoints[row][2] = valuez;
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
            for(int col=0;col<matrixOfPoints[0].length;col++){
                writer.write(String.valueOf(matrixOfPoints[row][col])+" ");
                //System.out.print(matrixOfPoints[row][col] + " ");
                if(col ==1){
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
