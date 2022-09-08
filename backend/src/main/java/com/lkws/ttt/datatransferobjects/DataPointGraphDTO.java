package com.lkws.ttt.datatransferobjects;

public record DataPointGraphDTO(
        String date,
        double price) {

    public static DataPointGraphDTO of(String date, double price) {
        return new DataPointGraphDTO(date, price);
    }
    public boolean equals(DataPointGraphDTO dataPointGraphDTO) {
        return dataPointGraphDTO.date.equals(date) && Double.compare(dataPointGraphDTO.price, price) == 0;
    }
}
