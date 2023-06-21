package utils;

import java.io.*;
import java.util.Random;

public class GenerateWorkloadSpecificationMatrix {
    private static double[][] vmMatrix, cloudletMatrix;
    private File vmFile = new File("VmSpecificationMatrix.txt");
    private File cloudletFile = new File("CloudletSpecificationMatrix.txt");

    public GenerateWorkloadSpecificationMatrix() {
        vmMatrix = new double[500][4]; // assuming 4 parameters: MIPS, RAM, BW, Size
        cloudletMatrix = new double[500][4]; // assuming 3 parameters: length, fileSize, outputSize, dcId

        try {
            if (vmFile.exists() && cloudletFile.exists()) {
                readSpecificationMatrix();
            } else {
                initSpecificationMatrix();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initSpecificationMatrix() throws IOException {
        System.out.println("Initializing new Specification Matrices...");
        BufferedWriter vmBufferedWriter = new BufferedWriter(new FileWriter(vmFile));
        BufferedWriter cloudletBufferedWriter = new BufferedWriter(new FileWriter(cloudletFile));

        // VM specifications: MIPS, RAM, BW, Size
        int[] mipsOptions = {200, 400, 600, 800}; // Decreased MIPS values
        int[] ramOptions = {256, 512, 1024}; // Decreased RAM values
        int[] bwOptions = {2000, 3000, 4000, 5000}; // Increased BW values
        long[] sizeOptions = {10000, 20000, 30000, 40000}; // Decreased Size values

        // Cloudlet specifications: length, fileSize, outputSize
        long[] lengthOptions = {200, 400, 600, 800, 1000, 1200, 1400, 1600}; // Decreased length options
        long[] outputSizeOptions = {100, 200, 300, 400}; // Decreased output size options

        Random rand = new Random();

        for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
            int randMipsIndex = rand.nextInt(mipsOptions.length);
            int randRamIndex = rand.nextInt(ramOptions.length);
            int randBwIndex = rand.nextInt(bwOptions.length);
            int randSizeIndex = rand.nextInt(sizeOptions.length);

            vmMatrix[i][0] = mipsOptions[randMipsIndex];
            vmMatrix[i][1] = ramOptions[randRamIndex];
            vmMatrix[i][2] = bwOptions[randBwIndex];
            vmMatrix[i][3] = sizeOptions[randSizeIndex];

            vmBufferedWriter.write(String.valueOf(vmMatrix[i][0]) + ' ');
            vmBufferedWriter.write(String.valueOf(vmMatrix[i][1]) + ' ');
            vmBufferedWriter.write(String.valueOf(vmMatrix[i][2]) + ' ');
            vmBufferedWriter.write(String.valueOf(vmMatrix[i][3]) + '\n');
        }

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int randLengthIndex = rand.nextInt(lengthOptions.length);
            int randOutputSizeIndex = rand.nextInt(outputSizeOptions.length);

            cloudletMatrix[i][0] = lengthOptions[randLengthIndex];
            // Set the fileSize to be a fraction of the VM's size, e.g. 1/4th
            cloudletMatrix[i][1] = vmMatrix[i][3] / 4;
            cloudletMatrix[i][2] = outputSizeOptions[randOutputSizeIndex];
            cloudletMatrix[i][3] = Math.random() * Constants.NO_OF_DATA_CENTERS;

            cloudletBufferedWriter.write(String.valueOf(cloudletMatrix[i][0]) + ' ');
            cloudletBufferedWriter.write(String.valueOf(cloudletMatrix[i][1]) + ' ');
            cloudletBufferedWriter.write(String.valueOf(cloudletMatrix[i][2]) + ' ');
            cloudletBufferedWriter.write(String.valueOf(cloudletMatrix[i][3]) + '\n');
        }


        vmBufferedWriter.close();
        cloudletBufferedWriter.close();
    }

    private void readSpecificationMatrix() throws IOException {
        System.out.println("Reading the Specification Matrices...");
        BufferedReader vmBufferedReader = new BufferedReader(new FileReader(vmFile));
        BufferedReader cloudletBufferedReader = new BufferedReader(new FileReader(cloudletFile));

        int i = 0, j = 0;
        do {
            String line = vmBufferedReader.readLine();
            for (String num : line.split(" ")) {
                vmMatrix[i][j++] = Double.parseDouble(num);
            }
            ++i;
            j = 0;
        } while (vmBufferedReader.ready());

        i = j = 0;
        do {
            String line = cloudletBufferedReader.readLine();
            for (String num : line.split(" ")) {
                cloudletMatrix[i][j++] = Double.parseDouble(num);
            }
            ++i;
            j = 0;
        } while (cloudletBufferedReader.ready());
    }

    public static double[][] getVmMatrix() {
        return vmMatrix;
    }

    public static double[][] getCloudletMatrix() {
        return cloudletMatrix;
    }
}
