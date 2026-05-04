package com.gudaocat.app.data.mock

import com.gudaocat.app.data.model.Cat
import com.gudaocat.app.data.model.Post
import com.gudaocat.app.data.model.User

object MockData {
    val currentUser = User(
        id = 1001,
        username = "校园观察员",
        bio = "今天也在校园里认真记录每一只猫",
    )

    val users = listOf(
        currentUser,
        User(
            id = 1002,
            username = "图书馆投喂员",
            bio = "负责记录图书馆和操场附近的猫咪动态。",
        ),
        User(
            id = 1003,
            username = "B座巡逻员",
            bio = "每天路过教学楼都会看看猫咪有没有出现。",
        ),
        User(
            id = 1004,
            username = "南区猫友",
            bio = "关注食堂后门和宿舍区的猫咪。",
        ),
    )

    val cats = listOf(
        Cat(
            id = 1,
            name = "橘座大人",
            habits = "常在午后晒太阳，性格亲人，喜欢被摸下巴。",
            location = "图书馆东侧花坛",
            photos = listOf("drawable/cat_juzuo"),
            creator_id = 1001,
            created_at = "2026-04-12 14:20",
        ),
        Cat(
            id = 2,
            name = "小黑炭",
            habits = "警惕性较高，会在固定投喂点等待熟悉的志愿者。",
            location = "操场看台下",
            photos = listOf("drawable/cat_xiaoheitan"),
            creator_id = 1002,
            created_at = "2026-04-10 17:35",
        ),
        Cat(
            id = 3,
            name = "奶牛猫",
            habits = "活泼好动，经常在教学楼门口与同学互动。",
            location = "教学楼 B 座门口",
            photos = listOf("drawable/cat_nainiucat"),
            creator_id = 1003,
            created_at = "2026-04-08 09:10",
        ),
        Cat(
            id = 4,
            name = "三花学姐",
            habits = "胆子较小，常躲在灌木丛附近观察路过的人。",
            location = "宿舍区 3 号楼旁",
            photos = listOf("drawable/cat_sanhua"),
            creator_id = 1001,
            created_at = "2026-04-06 18:45",
        ),
        Cat(
            id = 5,
            name = "饭团",
            habits = "饭点准时出现在食堂后门，体型圆润，叫声很轻。",
            location = "南区食堂后门",
            photos = listOf("drawable/cat_fantuan"),
            creator_id = 1004,
            created_at = "2026-04-03 12:05",
        ),
    )

    val posts = listOf(
        Post(
            id = 1,
            user_id = 1002,
            content = "今天在图书馆东侧遇到橘座大人，状态很好，已经补充了干粮和清水。",
            images = listOf("drawable/post_library_juzuo"),
            like_count = 42,
            created_at = "2026-04-25 16:20",
        ),
        Post(
            id = 2,
            user_id = 1003,
            content = "操场的小黑炭今天终于愿意靠近一点了，建议大家慢慢接触，不要突然伸手。",
            images = listOf("drawable/post_playground_xiaoheitan"),
            like_count = 128,
            created_at = "2026-04-24 19:08",
        ),
        Post(
            id = 3,
            user_id = 1001,
            content = "B 座门口的奶牛猫又在拦路撒娇，已确认精神状态正常。",
            images = listOf("drawable/post_building_nainiucat"),
            like_count = 67,
            created_at = "2026-04-24 10:32",
        ),
        Post(
            id = 4,
            user_id = 1004,
            content = "南区食堂后门新发现一只疑似未建档的小橘猫，已经拍照记录，准备补充档案。",
            images = listOf("drawable/post_canteen_orange"),
            like_count = 31,
            created_at = "2026-04-23 12:40",
        ),
    )

    private val postCatRelations = mapOf(
        1 to 1,
        2 to 2,
        3 to 3,
        4 to 1,
    )

    val recognizedCat = cats.first()

    fun catById(id: Int): Cat? = cats.firstOrNull { it.id == id }

    fun postById(id: Int): Post? = posts.firstOrNull { it.id == id }

    fun userById(id: Int): User? = users.firstOrNull { it.id == id }

    fun catForPost(postId: Int): Cat? = postCatRelations[postId]?.let(::catById)

    fun catsByCreator(userId: Int): List<Cat> = cats.filter { it.creator_id == userId }

    fun postsByUser(userId: Int): List<Post> = posts.filter { it.user_id == userId }

    const val demoToken = "demo-token"
    const val followingCount = 12
    const val followerCount = 36
    const val postCount = 8
}
