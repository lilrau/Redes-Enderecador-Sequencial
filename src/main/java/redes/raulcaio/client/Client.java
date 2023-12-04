package redes.raulcaio.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import redes.raulcaio.tools.Ansi_colors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Redes de computadores 2023/2
// Raul Souza
// Caio Furlan

public class Client {

    public static void main(String[] args) throws IOException {
        Ansi_colors colors = new Ansi_colors();
        System.out.println(colors.getColor("cyan") + "→ Raul Souza" + colors.getColor("default"));
        System.out.println(colors.getColor("cyan") + "→ Caio Furlan" + colors.getColor("default"));
        System.out.println();

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("IP do servidor:");
        String serverHostname = stdIn.readLine();
        System.out.println("Porta do servidor:");
        int serverPort = Integer.parseInt(stdIn.readLine());

        try {
            while (true) {
                try (Socket socket = new Socket(serverHostname, serverPort)) {
                    PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    System.out.println("Digite a Quantidade de Redes:");
                    String qtdRedes = stdIn.readLine();

                    // Criar o JSON
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.createObjectNode();

                    ArrayNode networkListNode = objectMapper.createArrayNode();

                    for (int i = 1; i <= Integer.parseInt(qtdRedes); i++) {
                        System.out.println("Digite a quantidade de máquinas da Rede" + colors.getColor("cyan") + " " + i + colors.getColor("default") + ":");
                        int qtdMaquinas = Integer.parseInt(stdIn.readLine());

                        ObjectNode networkNode = objectMapper.createObjectNode();
                        networkNode.put("id", i);
                        networkNode.put("maquinas", qtdMaquinas);
                        networkListNode.add(networkNode);
                    }

                    String jsonRequest = "";
                    ((ObjectNode) jsonNode).set("redes", networkListNode);
                    jsonRequest = objectMapper.writeValueAsString(jsonNode);

                    // Enviar o JSON para o servidor
                    sendRequest(outToServer, jsonRequest);
                    System.out.println(colors.getColor("yellow") + "→ Pedido Enviado:" + colors.getColor("default") + jsonRequest);

                    // Receber resposta do servidor
                    String serverResponse = getResponse(inFromServer);
                    System.out.println(colors.getColor("green") + "→ Resposta do Servidor:" + colors.getColor("default") + serverResponse);

                    // Mapear o JSON para um objeto Java
                    objectMapper = new ObjectMapper();
                    jsonNode = objectMapper.readTree(serverResponse);

                    ArrayNode networkList = (ArrayNode) jsonNode.get("redes");

                    System.out.println("-----------------------------------------------------------------------------");

                    // Exibir os endereços das redes
                    for (JsonNode networkNode: networkList) {
                        NetworkModel network = objectMapper.treeToValue(networkNode, NetworkModel.class);
                        System.out.println("Endereço da Rede " + colors.getColor("cyan") + network.getId() + colors.getColor("default") + ":");
                        System.out.println("↳ " + network.getEndereco());
                        System.out.println();
                    }

                    System.out.println("-----------------------------------------------------------------------------");
                    System.out.println(colors.getColor("green") + "→ Sucesso. Iniciando nova consulta." + colors.getColor("default"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getResponse(BufferedReader inFromServer) {
        Ansi_colors colors = new Ansi_colors();
        try {
            String serverResponse = inFromServer.readLine();
            return serverResponse;
        } catch (IOException e) {
            System.err.println(colors.getColor("red") + "Error ao ler mensagem do servidor: " + colors.getColor("default") + e.getMessage());
            return "";
        }
    }

    private static void sendRequest(PrintWriter outToServer, String jsonRequest) {
        outToServer.println(jsonRequest);
        outToServer.flush();
    }
}
