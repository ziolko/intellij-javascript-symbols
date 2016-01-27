package com.webstorm.symbols.index;

import com.google.common.collect.Maps;
import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.util.Consumer;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.webstorm.symbols.SymbolUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

public class JSSymbolsIndex extends FileBasedIndexExtension<String, Integer> {
    public static final ID<String, Integer> INDEX_ID = ID.create("js.symbols.index");
    public static final DataExternalizer<Integer> INTEGER_DATA_EXTERNALIZER = new IntegerDataExternalizer();

    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();
    private final DataIndexer<String, Integer, FileContent> myIndexer = inputData -> {
        Map<String, Integer> result = Maps.newHashMap();
        SymbolUtils.processSymbolsInPsiFile(inputData.getPsiFile(), element -> {
            final String symbol = SymbolUtils.getSymbolFromPsiElement(element);
            result.put(symbol, result.getOrDefault(symbol, 0) + 1);
            return true;
        });
        return result;
    };

    @NotNull
    @Override
    public ID<String, Integer> getName() {
        return INDEX_ID;
    }

    @NotNull
    @Override
    public DataIndexer<String, Integer, FileContent> getIndexer() {
        return myIndexer;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return myKeyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<Integer> getValueExternalizer() {
        return INTEGER_DATA_EXTERNALIZER;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
         return new FileBasedIndex.FileTypeSpecificInputFilter() {
             @Override
             public void registerFileTypesUsedForIndexing(@NotNull Consumer<FileType> fileTypeSink) {
                 JavaScriptFileType.getFileTypesCompilableToJavaScript().forEach(fileTypeSink::consume);
             }

             @Override
             public boolean acceptInput(@NotNull VirtualFile file) {
                 return true;
             }
         };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 5;
    }

    private static class IntegerDataExternalizer implements DataExternalizer<Integer> {
        @Override
        public void save(@NotNull DataOutput out, Integer value) throws IOException {
            out.writeInt(value);
        }

        @Override
        @Nullable
        public Integer read(@NotNull final DataInput in) throws IOException {
            return in.readInt();
        }
    }
}
