package redes.raulcaio.calculator;

import java.util.Scanner;

public class NetworkAddressCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String baseIPAddress = "192.168.0.0";

        System.out.print("Enter the number of networks: ");
        int numNetworks = scanner.nextInt();

        int currentNetworkIndex = 0;

        for (int i = 1; i <= numNetworks; i++) {
            System.out.print("Enter the number of machines in network " + i + ": ");
            int numMachines = scanner.nextInt();

            String networkAddress = calculateNetworkAddress(baseIPAddress, currentNetworkIndex);
            int subnetMask = calculateSubnetMask(numMachines);

            System.out.println("Network " + i + ": " + networkAddress + "/" + subnetMask);

            // Move o index atual para o proximo endereço de rede disponível
            currentNetworkIndex += 1 << (32 - subnetMask);
        }

        scanner.close();
    }

    public static String calculateNetworkAddress(String baseIPAddress, int offset) {
        String[] parts = baseIPAddress.split("\\.");
        int[] ipParts = new int[4];

        for (int i = 0; i < 4; i++) {
            ipParts[i] = Integer.parseInt(parts[i]);
        }

        // Calcula o proximo endereço de rede baseado no offset
        ipParts[2] += offset / 256;
        ipParts[3] += offset % 256;

        return String.format("%d.%d.%d.%d", ipParts[0], ipParts[1], ipParts[2], ipParts[3]);
    }

    public static int calculateSubnetMask(int numMachines) {
        int totalAddressesNeeded = Integer.highestOneBit(numMachines) << 1;
        return 32 - Integer.numberOfTrailingZeros(totalAddressesNeeded);
    }
}
