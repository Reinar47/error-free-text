package ru.example.errorfreetext.service;

import org.junit.jupiter.api.Test;
import ru.example.errorfreetext.Utils.TextChunker;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TextChunkerTest {

    private final TextChunker textChunker = new TextChunker();

    @Test
    void splitsLongTextIntoChunks() {
        String text = "a".repeat(25_000);

        List<String> chunks = textChunker.split(text);

        assertThat(chunks).hasSize(3);
        chunks.forEach(chunk -> assertThat(chunk.length()).isLessThanOrEqualTo(TextChunker.MAX_CHUNK_SIZE));
    }

    @Test
    void keepsShortTextAsSingleChunk() {
        String text = "hello world";

        List<String> chunks = textChunker.split(text);

        assertThat(chunks).containsExactly("hello world");
    }

    @Test
    void returnsEmptyListForEmptyInput() {
        assertThat(textChunker.split("")).isEmpty();
        assertThat(textChunker.split(null)).isEmpty();
    }

    @Test
    void doesNotSplitWordsWhenPossible() {
        String longWord = "a".repeat(9_999);
        String text = longWord + " " + "b".repeat(9_999);

        List<String> chunks = textChunker.split(text);

        assertThat(chunks).hasSize(2);
        assertThat(chunks.get(0)).isEqualTo(longWord);
        assertThat(chunks.get(1)).isEqualTo("b".repeat(9_999));
    }
}