package Indexing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class FileIndex {

    private static Path baseDirectory;
    private IndexSearcher searcher;

    public FileIndex(String path) {
        baseDirectory = Paths.get(path);
        tryCreateIndex();
    }

    /**
     * Creates a lucene index using the constant file name at the constant file path.
     */
    private void tryCreateIndex() {
        try {
            Directory dir = FSDirectory.open(baseDirectory);

            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            IndexWriter writer = new IndexWriter(dir, iwc);
            writer.deleteAll();
            writer.commit();

            ArrayList<Document> pcmFiles = indexPCMFiles(baseDirectory);

            for (Document doc : pcmFiles) {
                writer.addDocument(doc);
            }

            writer.commit();
            writer.close();

            IndexReader reader = DirectoryReader.open(FSDirectory.open(baseDirectory));
            searcher = new IndexSearcher(reader);

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private ArrayList<Document> indexPCMFiles(Path directoryPath) {
        ArrayList<Document> indexedDocs = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            paths.forEach((path -> indexedDocs.add(pcmToDocument(path))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return indexedDocs;
    }

    private Document pcmToDocument(Path filePath) {
        System.out.println(filePath);
        Document doc = new Document();

        for (int i = baseDirectory.getNameCount(); i < filePath.getNameCount(); i++) {
            String pathElement = filePath.getName(i).toString();
            if (!pathElement.contains("."))
                doc.add(new TextField("folder", pathElement, Field.Store.YES));
            else
                doc.add(new TextField("name", pathElement, Field.Store.YES));
        }
        return doc;
    }
}
