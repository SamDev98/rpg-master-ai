package com.rpgmaster.app.adapter.inbound;

import com.rpgmaster.app.application.QueryUseCase;
import com.rpgmaster.domain.QueryRequest;
import com.rpgmaster.domain.SourceChunk;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Spring Shell command for querying a rulebook via the RAG pipeline.
 *
 * <p>Usage:
 * <pre>{@code
 * rpg:> ask --question "What is the range of a Fireball spell?" --rulebook dnd-5e-phb
 * }</pre>
 */
@ShellComponent
public class AskCommand {

    private final QueryUseCase queryUseCase;

    public AskCommand(QueryUseCase queryUseCase) {
        this.queryUseCase = queryUseCase;
    }

    @ShellMethod(value = "Ask a question about a rulebook", key = "ask")
    public String ask(
            @ShellOption(help = "Your natural language question") String question,
            @ShellOption(defaultValue = ShellOption.NULL,
                    help = "Rulebook ID to search (omit to search all rulebooks)") String rulebook,
            @ShellOption(defaultValue = "5", help = "Number of context chunks to retrieve") int topK,
            @ShellOption(defaultValue = "0.3", help = "Minimum similarity threshold (0.0-1.0)") float threshold
    ) {
        var request = new QueryRequest(question, rulebook, topK, threshold);
        var result = queryUseCase.query(request);

        var sb = new StringBuilder();
        sb.append("\n━━━ Answer ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append(result.answer());
        sb.append("\n\n━━━ Sources (%d chunks) ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
                .formatted(result.sources().size()));

        for (SourceChunk source : result.sources()) {
            sb.append("  • [Page %d | score=%.2f | %s] %s%n"
                    .formatted(source.pageNumber(), source.score(),
                            source.rulebookId(),
                            truncate(source.text(), 120)));
        }

        sb.append("\n  Latency: %dms\n".formatted(result.latencyMs()));

        return sb.toString();
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "…";
    }
}
