package org.demo.seniorjavatechchallenge.domain.model;

import java.time.LocalDate;

public final class DateRange {
    private final LocalDate start;
    private final LocalDate end; // Puede ser null para rango abierto

    public DateRange(LocalDate start, LocalDate end) {
        if (start == null) throw new IllegalArgumentException("Start date required");
        if (end != null && !start.isBefore(end))
            throw new IllegalArgumentException("Start date must be before end date");
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() { return start; }
    public LocalDate getEnd() { return end; }

    public boolean overlaps(DateRange other) {
        if (other == null) return false;
        LocalDate thisEnd = this.end != null ? this.end : LocalDate.MAX;
        LocalDate otherEnd = other.end != null ? other.end : LocalDate.MAX;
        return !this.start.isAfter(otherEnd) && !other.start.isAfter(thisEnd);
    }
}

