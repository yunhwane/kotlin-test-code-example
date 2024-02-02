package com.example.testexample.rank

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest


/**
 * Created by yunhwan on 2024-02-01
 *
 * 테스트 요구사항
 * 1. 만약 회원중에 3개의 등급 회원이 있다고 가정하자.
 * 2. 등급은 VIP, GOLD, SILVER가 있다.
 * 3. VIP는 10%, GOLD는 7%, SILVER는 5% 할인율을 적용한다.
 * 4. 등급에 따라 할인율을 적용한 가격을 반환하는 기능을 구현하라.
 *
 * test code 2장
 * 1. 테스트를 작성한다.
 * -> 이야기를 써내려가며 작성한다.
 * -> 오퍼레이션(메서드)를 개발해라
 *
 * 2. 실행가능하게 만들어라
 * -> 초록 막대를 만들게 빠르게 작성하라
 * -> 쓰레기 같이 만들어도 초록막대를 보게 작성하고 초록막대를 보자
 *
 * 3. 리팩토링을 하라 (중복코드를 고쳐라) 무조건으로 !
 * -> 리팩토링을 하면서 테스트를 돌려라
 * -> 쓰레기 같은 코드를 고쳐라
 *
 * 여기서 생각한점 !
 *
 * private val discountCalculator: DiscountCalculator = when (rank) {
 *             Grade.VIP -> VipDiscountCalculator()
 *             Grade.GOLD -> GoldDiscountCalculator()
 *             Grade.SILVER -> SilverDiscountCalculator()
 *             else -> {
 *                 throw IllegalArgumentException("등급이 없습니다.")}
 *
 * DiscountCalculator를 클래스로 사용하여 추상화를 시켜서 등급별로 할인율을 계산하는 클래스를 만들어서 처리했다.
 * 하지만 여기서 봤을 때, Member 클래스에서 DiscountCalculator를 사용하는데, 이것을 사용하는 것이 좋은지에 대한 고민이 필요했다.
 * OCP 원칙에 위배되는 것 같아서, 이것을 해결하기 위해서는 Member 클래스에서 DiscountCalculator를 사용하는 것이 아니라, DiscountCalculator를 사용하는 것이 좋다고 생각했다.
 * 그래서 enum 클래스에서 DiscountCalculator를 사용하는 것이 좋다고 생각해서 enum에 상속받아서 바로 설계하였다.
 * 등급을 고쳐 추가하거나 변경할 때, DiscountCalculator를 상속받아서 구현하면 된다.
 * 또한 테스트 코드를 고치지 않아도 변경했을 때 바로 적용되는 방법을 생각해보자.
 *
 */

@SpringBootTest
class RankTest {

    @Test
    fun `등급에 따라서 할인율 적용 회원 테스트_VIP`() {
        /**
         * 1. 등급을 가진 회원이 존재해야한다.
         */
        //given
        val member = Member("yunhwan", Grade.VIP)

        //then
        val discountPrice = member.getDiscountPrice(10000)

        assertThat(discountPrice).isEqualTo(9000)
    }

    @Test
    fun `등급에 따라서 할인율 적용 회원 테스트_GOLD`() {
        /**
         * 1. 등급을 가진 회원이 존재해야한다.
         */
        //given
        val member = Member("yunhwan", Grade.GOLD)

        //then
        val orderPrice : Int = 10000
        val discountPrice = member.getDiscountPrice(orderPrice)

        assertThat(discountPrice).isEqualTo(10000 - (10000 * 0.07).toInt())
    }

    @Test
    fun `등급에 따라서 할인율 적용 회원 테스트_SILVER`() {
        /**
         * 1. 등급을 가진 회원이 존재해야한다.
         */
        //given
        val member = Member("yunhwan", Grade.SILVER)

        //then
        val orderPrice : Int = 10000
        val discountPrice = member.getDiscountPrice(orderPrice)

        assertThat(discountPrice).isEqualTo(10000 - (10000 * 0.05).toInt())
    }



    // domain 객체
    class Member(name: String, rank: Grade) {
        private var name = name
        private var rank = rank


        fun getDiscountPrice(orderPrice: Int): Int {
            return orderPrice - this.rank.discountCalculator().calculateDiscount(orderPrice)
        }

    }


    enum class Grade {
        VIP {
            override fun discountCalculator(): DiscountCalculator = VipDiscountCalculator()
        },
        GOLD {
            override fun discountCalculator(): DiscountCalculator = GoldDiscountCalculator()
        },
        SILVER {
            override fun discountCalculator(): DiscountCalculator = SilverDiscountCalculator()
        };

        abstract fun discountCalculator(): DiscountCalculator
    }

    /*
    등급별로 추상화
     */
    interface DiscountCalculator {
        fun calculateDiscount(orderPrice: Int): Int
    }

    // VIP 등급 할인 계산 구현
    class VipDiscountCalculator : DiscountCalculator {
        override fun calculateDiscount(orderPrice: Int): Int {
            return (orderPrice * 0.1).toInt()
        }
    }

    // GOLD 등급 할인 계산 구현
    class GoldDiscountCalculator : DiscountCalculator {
        override fun calculateDiscount(orderPrice: Int): Int {
            return (orderPrice * 0.07).toInt() // GOLD는 7% 할인
        }
    }

    // SILVER 등급 할인 계산 구현
    class SilverDiscountCalculator : DiscountCalculator {
        override fun calculateDiscount(orderPrice: Int): Int {
            return (orderPrice * 0.05).toInt() // SILVER는 5% 할인
        }
    }



}