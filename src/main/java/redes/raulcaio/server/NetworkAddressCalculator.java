package redes.raulcaio.server;

public class NetworkAddressCalculator {

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
