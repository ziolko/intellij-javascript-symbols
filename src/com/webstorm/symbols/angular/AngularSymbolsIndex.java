package com.webstorm.symbols.angular;

import com.google.common.collect.Maps;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AngularSymbolsIndex extends ScalarIndexExtension<String> {
    public static final ID<String, Void> INDEX_ID = ID.create("angular.symbols.index");

    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    private final DataIndexer<String, Void, FileContent> myIndexer = new DataIndexer<String, Void, FileContent>() {
        @NotNull
        @Override
        public Map<String, Void> map(final @NotNull FileContent inputData) {
            final Map<String, Void> result = Maps.newHashMap();

            if(!AngularSymbolUtils.isAngularPluginEnabled()) {
                return result;
            }

            for(final String symbol : AngularSymbolUtils.getSymbolsInPlainText(inputData.getPsiFile().getText())) {
                result.put(symbol, null);
            }

            return result;
        }
    };

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return INDEX_ID;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return myIndexer;
    }


    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return myKeyDescriptor;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new FileBasedIndex.FileTypeSpecificInputFilter() {
            @Override
            public void registerFileTypesUsedForIndexing(@NotNull Consumer<FileType> fileTypeSink) {
                fileTypeSink.consume(StdFileTypes.HTML);
                fileTypeSink.consume(StdFileTypes.XHTML);
            }

            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                return file.getFileSystem() == LocalFileSystem.getInstance() ;
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 0;
    }

}
