package redes.raulcaio.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import redes.raulcaio.tools.Ansi_colors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static redes.raulcaio.server.NetworkAddressCalculator.calculateNetworkAddress;
import static redes.raulcaio.server.NetworkAddressCalculator.calculateSubnetMask;

// Redes de computadores 2023/2
// Raul Souza
// Caio Furlan

public class Server {

    public static void main(String[] args) throws IOException {
        Ansi_colors colors = new Ansi_colors();
        System.out.println(colors.getColor("cyan") + "→ Raul Souza" + colors.getColor("default"));
        System.out.println(colors.getColor("cyan") + "→ Caio Furlan" + colors.getColor("default"));
        System.out.println();

        System.out.println("IP do servidor: " + InetAddress.getLocalHost().getHostAddress());

        ServerSocket serverSocket = null;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nPorta do servidor:");
        int serverPort = Integer.parseInt(stdIn.readLine());
        try {
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Servidor esperando por conexões...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(colors.getColor("green") + "\n→ Cliente conectado: " + socket.getInetAddress() + colors.getColor("default"));

                // Tratar a solicitação do cliente em uma thread separada
                new Thread(() -> {
                    handleClientRequest(socket);
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Não feche o socket aqui, pois isso encerraria a conexão para todos os clientes
        }
    }

    private static void handleClientRequest(Socket socket) {
        try {
            Ansi_colors colors = new Ansi_colors();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ObjectMapper objectMapper = new ObjectMapper();

            // Ler a solicitação do cliente
            String clientRequest = inFromClient.readLine();
            JsonNode clientData = objectMapper.readTree(clientRequest);

            // Imprimir a solicitação do cliente
            System.out.println(colors.getColor("yellow") + "→ Solicitação do cliente: " + colors.getColor("default") + clientData);

            // Obter o número de redes e a quantidade de máquinas de cada rede
            JsonNode redes = clientData.get("redes");
            int n_redes = redes.size();

            ObjectNode response = objectMapper.createObjectNode();

            // Calcular a máscara e o endereço de rede para cada rede
            int currentNetworkIndex = 0;
            // Criando e organizando a lista de redes em ordem decrescente de quantidade de máquinas
            List<NetworkModel> networkList = new ArrayList<>();
            for (JsonNode rede : redes) {
                networkList.add(objectMapper.convertValue(rede, NetworkModel.class));
            }
            networkList.sort((a, b) -> b.getMaquinas() - a.getMaquinas());
            for (NetworkModel network : networkList) {
                String networkAddress = calculateNetworkAddress("192.168.0.0", currentNetworkIndex);
                int subnetMask = calculateSubnetMask(network.getMaquinas());

                networkAddress = networkAddress + "/" + subnetMask;
                network.setEndereco(networkAddress);

                // Move o index atual para o proximo endereço de rede disponível
                currentNetworkIndex += 1 << (32 - subnetMask);
            }
            // Voltando a lista para a ordem original
            networkList.sort((a, b) -> a.getId() - b.getId());

            // Enviar a resposta para o cliente
            response.set("redes", objectMapper.valueToTree(networkList));
            String serverResponse = objectMapper.writeValueAsString(response);
            out.println(serverResponse);
            System.out.println(colors.getColor("green") + "→ Resposta enviada para o cliente: " + colors.getColor("default") + serverResponse);

            System.out.println("----------------------------------------------------------------------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Fechar o socket individualmente após o tratamento da solicitação
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}