package exalt.kata.domain;

public enum Currency
{
    AED(1), AFN(2), ALL(3), AMD(4), ANG(5), AOA(6), ARS(7), AUD(8), AWG(9), AZN(10),
    BAM(11), BBD(12), BDT(13), BGN(14), BHD(15), BIF(16), BMD(17), BND(18), BOB(19), BOV(20),
    BRL(21), BSD(22), BTN(23), BWP(24), BYN(25), BYR(26), BZD(27), CAD(28), CDF(29), CHE(30),
    CHF(31), CHW(32), CLF(33), CLP(34), CNY(35), COP(36), COU(37), CRC(38), CUC(39), CUP(40),
    CVE(41), CZK(42), DJF(43), DKK(44), DOP(45), DZD(46), EGP(47), ERN(48), ETB(49), EUR(50),
    FJD(51), FKP(52), GBP(53), GEL(54), GHS(55), GIP(56), GMD(57), GNF(58), GTQ(59), GYD(60),
    HKD(61), HNL(62), HRK(63), HTG(64), HUF(65), IDR(66), ILS(67), INR(68), IQD(69), IRR(70),
    ISK(71), JMD(72), JOD(73), JPY(74), KES(75), KGS(76), KHR(77), KMF(78), KPW(79), KRW(80),
    KWD(81), KYD(82), KZT(83), LAK(84), LBP(85), LKR(86), LRD(87), LSL(88), LYD(89), MAD(90),
    MDL(91), MGA(92), MKD(93), MMK(94), MNT(95), MOP(96), MRO(97), MUR(98), MVR(99), MWK(100),
    MXN(101), MXV(102), MYR(103), MZN(104), NAD(105), NGN(106), NIO(107), NOK(108), NPR(109), NZD(110),
    OMR(111), PAB(112), PEN(113), PGK(114), PHP(115), PKR(116), PLN(117), PYG(118), QAR(119), RON(120),
    RSD(121), RUB(122), RWF(123), SAR(124), SBD(125), SCR(126), SDG(127), SEK(128), SGD(129), SHP(130),
    SLL(131), SOS(132), SRD(133), SSP(134), STD(135), SYP(136), SZL(137), THB(138), TJS(139), TMT(140),
    TND(141), TOP(142), TRY(143), TTD(144), TWD(145), TZS(146), UAH(147), UGX(148), USD(149), USN(150),
    UYI(151), UYU(152), UZS(153), VEF(154), VND(155), VUV(156), WST(157), XAF(158), XAG(159), XAU(160),
    XBA(161), XBB(162), XBC(163), XBD(164), XCD(165), XDR(166), XFU(167), XOF(168), XPD(169), XPF(170),
    XPT(171), XSU(172), XTS(173), XUA(174), XXX(175), YER(176), ZAR(177), ZMW(178);
    private final int code;

    Currency(int code)
    {
        this.code = code;
    }

    @Override
    public String toString()
    {
        return name();
    }
}