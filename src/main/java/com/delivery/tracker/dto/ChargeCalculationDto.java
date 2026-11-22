package com.delivery.tracker.dto;

public class ChargeCalculationDto {
    private Double volumetricWeight;
    private Double chargeableWeight;
    private Double baseCharge;
    private Double codSurcharge;
    private Double totalCharge;
    private String pickupZoneName;
    private String dropZoneName;

    public ChargeCalculationDto() {}

    public ChargeCalculationDto(Double volumetricWeight, Double chargeableWeight, Double baseCharge,
                                Double codSurcharge, Double totalCharge, String pickupZoneName, String dropZoneName) {
        this.volumetricWeight = volumetricWeight;
        this.chargeableWeight = chargeableWeight;
        this.baseCharge = baseCharge;
        this.codSurcharge = codSurcharge;
        this.totalCharge = totalCharge;
        this.pickupZoneName = pickupZoneName;
        this.dropZoneName = dropZoneName;
    }

    public Double getVolumetricWeight() { return volumetricWeight; }
    public void setVolumetricWeight(Double volumetricWeight) { this.volumetricWeight = volumetricWeight; }
    public Double getChargeableWeight() { return chargeableWeight; }
    public void setChargeableWeight(Double chargeableWeight) { this.chargeableWeight = chargeableWeight; }
    public Double getBaseCharge() { return baseCharge; }
    public void setBaseCharge(Double baseCharge) { this.baseCharge = baseCharge; }
    public Double getCodSurcharge() { return codSurcharge; }
    public void setCodSurcharge(Double codSurcharge) { this.codSurcharge = codSurcharge; }
    public Double getTotalCharge() { return totalCharge; }
    public void setTotalCharge(Double totalCharge) { this.totalCharge = totalCharge; }
    public String getPickupZoneName() { return pickupZoneName; }
    public void setPickupZoneName(String pickupZoneName) { this.pickupZoneName = pickupZoneName; }
    public String getDropZoneName() { return dropZoneName; }
    public void setDropZoneName(String dropZoneName) { this.dropZoneName = dropZoneName; }

    public static ChargeCalculationDtoBuilder builder() { return new ChargeCalculationDtoBuilder(); }

    public static class ChargeCalculationDtoBuilder {
        private Double volumetricWeight, chargeableWeight, baseCharge, codSurcharge, totalCharge;
        private String pickupZoneName, dropZoneName;

        public ChargeCalculationDtoBuilder volumetricWeight(Double v) { this.volumetricWeight = v; return this; }
        public ChargeCalculationDtoBuilder chargeableWeight(Double v) { this.chargeableWeight = v; return this; }
        public ChargeCalculationDtoBuilder baseCharge(Double v) { this.baseCharge = v; return this; }
        public ChargeCalculationDtoBuilder codSurcharge(Double v) { this.codSurcharge = v; return this; }
        public ChargeCalculationDtoBuilder totalCharge(Double v) { this.totalCharge = v; return this; }
        public ChargeCalculationDtoBuilder pickupZoneName(String v) { this.pickupZoneName = v; return this; }
        public ChargeCalculationDtoBuilder dropZoneName(String v) { this.dropZoneName = v; return this; }
        public ChargeCalculationDto build() {
            return new ChargeCalculationDto(volumetricWeight, chargeableWeight, baseCharge, codSurcharge, totalCharge, pickupZoneName, dropZoneName);
        }
    }
}