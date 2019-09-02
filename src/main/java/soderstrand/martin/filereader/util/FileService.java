package soderstrand.martin.filereader.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import soderstrand.martin.filereader.model.Result;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FileService {

    @Value("${scan.folder.path}")
    private String folderPath;

    public void manageFile(Path path) {
        Result fileResult = parseFile(path);
        if (fileResult != null) {
            createOutputFile(fileResult);
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Result parseFile(Path path) {
        boolean filesWasCorrectlyParsed = true;
        Result result = new Result();
        result.setFileName(path.toFile().getName().replaceAll(".txt", ""));

        try (Stream<String> lineStream = Files.lines(path)) {
            List<String> lines = lineStream.collect(Collectors.toList());

            if (!lines.isEmpty()) {
                Set<String> clients = new HashSet<>();
                Set<String> sellers = new HashSet<>();
                Map<String, Double> sellersAndSalesAmount = new HashMap<>();
                Map<Integer, Double> salesIdAndSalesAmount = new HashMap<>();
                for (String line : lines) {
                    String[] linePieces = line.split(",");

                    if (linePieces.length == 2) {
                        String sellerName = linePieces[1];
                        sellers.add(sellerName);
                    } else if (linePieces.length == 3) {
                        clients.add(linePieces[2]);
                    } else if (linePieces.length == 4) {
                        String[] orderObjects = linePieces[2].split(";");

                        if (orderObjects.length == 3) {
                            for (String orderObject : orderObjects) {

                                String[] orderProperties = orderObject.split("-");
                                if (orderProperties.length == 3) {
                                    Double salesAmount = Double.valueOf(orderProperties[2].replaceAll("]", "").replaceAll("\\[", ""));
                                    String sellerName = linePieces[3].replaceAll("]", "").replaceAll("\\[", "");
                                    Integer salesId = Integer.valueOf(linePieces[1].replaceAll("]", "").replaceAll("\\[", ""));

                                    sellersAndSalesAmount.put(sellerName, sellersAndSalesAmount.getOrDefault(sellerName, 0D) + salesAmount);
                                    salesIdAndSalesAmount.put(salesId, salesIdAndSalesAmount.getOrDefault(salesId, 0D) + salesAmount);
                                } else {
                                    filesWasCorrectlyParsed = false;
                                    break;
                                }
                            }
                        } else {
                            filesWasCorrectlyParsed = false;
                            break;
                        }
                    } else {
                        filesWasCorrectlyParsed = false;
                        break;
                    }
                }
                setLowestSellerName(sellersAndSalesAmount, result);
                setHighestSalesId(salesIdAndSalesAmount, result);
                result.setNumberOfClients(clients.size());
                result.setNumberOfSellers(sellers.size());

            } else
                filesWasCorrectlyParsed = false;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(String.format("Could not parse file '%s' due to bad format", path.toFile().getName()));
            return null;
        }
        return filesWasCorrectlyParsed ? result : null;
    }

    private void setLowestSellerName(Map<String, Double> sellersAndSalesAmount, Result result) {
        if (!sellersAndSalesAmount.values().isEmpty()) {
            double lowestSellerSalesAmount = Collections.min(sellersAndSalesAmount.values());

            if (lowestSellerSalesAmount != 0)
                sellersAndSalesAmount.entrySet().stream()
                        .filter(entry -> entry.getValue() == lowestSellerSalesAmount)
                        .map(Map.Entry::getKey).findFirst().ifPresent(result::setNameOfWorstSeller);
        }
    }

    private void setHighestSalesId(Map<Integer, Double> salesIdAndSalesAmount, Result result) {
        if (!salesIdAndSalesAmount.values().isEmpty()) {
            double highestSalesIdSalesAmount = Collections.max(salesIdAndSalesAmount.values());

            if (highestSalesIdSalesAmount != 0)
                salesIdAndSalesAmount.entrySet().stream()
                        .filter(entry -> entry.getValue() == highestSalesIdSalesAmount)
                        .map(Map.Entry::getKey).findFirst().ifPresent(result::setSalesIdOfBiggestSale);
        }
    }

    private void createOutputFile(Result result) {
        Path path = Paths.get(folderPath + "/" + result.getFileName() + ".done.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("Number of Clients found in the file: " + result.getNumberOfClients());
            writer.newLine();
            writer.write("Number of Sellers found in the file: " + result.getNumberOfSellers());
            writer.newLine();
            writer.write("Sales id of the biggest sale: " + result.getSalesIdOfBiggestSale());
            writer.newLine();
            writer.write("Name of the Seller that sold less: " + result.getNameOfWorstSeller());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
