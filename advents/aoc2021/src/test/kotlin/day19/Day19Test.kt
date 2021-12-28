package com.gilpereda.adventsofcode.adventsofcode2021.day19

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day19Test : BaseTest() {
    override val example: String = """
        --- scanner 0 ---
        404,-588,-901
        528,-643,409
        -838,591,734
        390,-675,-793
        -537,-823,-458
        -485,-357,347
        -345,-311,381
        -661,-816,-575
        -876,649,763
        -618,-824,-621
        553,345,-567
        474,580,667
        -447,-329,318
        -584,868,-557
        544,-627,-890
        564,392,-477
        455,729,728
        -892,524,684
        -689,845,-530
        423,-701,434
        7,-33,-71
        630,319,-379
        443,580,662
        -789,900,-551
        459,-707,401

        --- scanner 1 ---
        686,422,578
        605,423,415
        515,917,-361
        -336,658,858
        95,138,22
        -476,619,847
        -340,-569,-846
        567,-361,727
        -460,603,-452
        669,-402,600
        729,430,532
        -500,-761,534
        -322,571,750
        -466,-666,-811
        -429,-592,574
        -355,545,-477
        703,-491,-529
        -328,-685,520
        413,935,-424
        -391,539,-444
        586,-435,557
        -364,-763,-893
        807,-499,-711
        755,-354,-619
        553,889,-390

        --- scanner 2 ---
        649,640,665
        682,-795,504
        -784,533,-524
        -644,584,-595
        -588,-843,648
        -30,6,44
        -674,560,763
        500,723,-460
        609,671,-379
        -555,-800,653
        -675,-892,-343
        697,-426,-610
        578,704,681
        493,664,-388
        -671,-858,530
        -667,343,800
        571,-461,-707
        -138,-166,112
        -889,563,-600
        646,-828,498
        640,759,510
        -630,509,768
        -681,-892,-333
        673,-379,-804
        -742,-814,-386
        577,-820,562

        --- scanner 3 ---
        -589,542,597
        605,-692,669
        -500,565,-823
        -660,373,557
        -458,-679,-417
        -488,449,543
        -626,468,-788
        338,-750,-386
        528,-832,-391
        562,-778,733
        -938,-730,414
        543,643,-506
        -524,371,-870
        407,773,750
        -104,29,83
        378,-903,-323
        -778,-728,485
        426,699,580
        -438,-605,-362
        -469,-447,-387
        509,732,623
        647,635,-688
        -868,-804,481
        614,-800,639
        595,780,-596

        --- scanner 4 ---
        727,592,562
        -293,-554,779
        441,611,-461
        -714,465,-776
        -743,427,-804
        -660,-479,-426
        832,-632,460
        927,-485,-438
        408,393,-506
        466,436,-512
        110,16,151
        -258,-428,682
        -393,719,612
        -211,-452,876
        808,-476,-593
        -575,615,604
        -485,667,467
        -680,325,-822
        -627,-443,-432
        872,-547,-609
        833,512,582
        807,604,487
        839,-516,451
        891,-625,532
        -652,-548,-490
        30,-46,-14
    """.trimIndent()

    override val result1: String = "79"

    override val result2: String = "3621"

    override val input: String = "/day19/input.txt"
    override val run1: Executable = part1
    override val run2: Executable = part2

    @Test
    fun `should parse the input`() {
        val input = """
        --- scanner 0 ---
        404,-588,-901
        528,-643,409
        -838,591,734
        390,-675,-793
        -537,-823,-458

        --- scanner 1 ---
        686,422,578
        605,423,415
        515,917,-361
        -336,658,858

        --- scanner 2 ---
        649,640,665
        682,-795,504
        -784,533,-524
        -644,584,-595
        """.trimIndent().split("\n")

        val expected = listOf(
            Scanner(id = 0, beacons = listOf(
                Beacon(Coord(404.0,-588.0,-901.0)),
                Beacon(Coord(528.0,-643.0,409.0)),
                Beacon(Coord(-838.0,591.0,734.0)),
                Beacon(Coord(390.0,-675.0,-793.0)),
                Beacon(Coord(-537.0,-823.0,-458.0)),
            )),
            Scanner(id = 1, beacons = listOf(
                Beacon(Coord(686.0, 422.0, 578.0)),
                Beacon(Coord(605.0,423.0, 415.0)),
                Beacon(Coord(515.0,917.0,-361.0)),
                Beacon(Coord(-336.0,658.0,858.0)),
            )),
            Scanner(id = 2, beacons = listOf(
                Beacon(Coord(649.0,640.0,665.0)),
                Beacon(Coord(682.0,-795.0,504.0)),
                Beacon(Coord(-784.0,533.0,-524.0)),
                Beacon(Coord(-644.0,584.0,-595.0)),
            ))
        )
        assertThat(parseInput(input)).isEqualTo(expected)
    }

    @Test
    fun `id transform should keep the coords untouched`() {
        val idT = Transformation(
            a11 = 1.0, a12 = 0.0, a13 = 0.0, a14 = 0.0,
            a21 = 0.0, a22 = 1.0, a23 = 0.0, a24 = 0.0,
            a31 = 0.0, a32 = 0.0, a33 = 1.0, a34 = 0.0,
        )

        val coord = Coord(321.0, -443.0, 665.0)

        assertThat(idT.transform(coord)).isEqualTo(coord)
    }

    @Test
    fun `should calculate the id transform`() {
        val idT = Transformation(
            a11 = 1.0, a12 = 0.0, a13 = 0.0, a14 = 0.0,
            a21 = 0.0, a22 = 1.0, a23 = 0.0, a24 = 0.0,
            a31 = 0.0, a32 = 0.0, a33 = 1.0, a34 = 0.0,
        )

        val coord1 = Coord(321.0, -443.0, 665.0)
        val coord2 = Coord(-124.0, 224.0, 180.0)
        val coord3 = Coord(123.0, -102.0, -365.0)
        val coord4 = Coord(421.0, -333.0, -175.0)
        val transf = Transformation.fromPoints(
            Pair(coord1, coord1.copy(z = coord1.z + 100)),
            Pair(coord2, coord2.copy(z = coord2.z + 100)),
            Pair(coord3, coord3.copy(z = coord3.z + 100)),
            Pair(coord4, coord4.copy(z = coord4.z + 100))
        )

//        assertThat(transf).isEqualTo(idT)

        assertThat(transf.transform(coord1)).isEqualTo(coord1)
    }
}