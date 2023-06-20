package com.example.storyapp

import com.example.storyapp.data.remote.story.StoryModel


object DataDummy {
    fun generateDummyStoryResponse(): List<StoryModel> {
        val storyList: MutableList<StoryModel> = arrayListOf()
        for (i in 0..100) {
            val story = StoryModel(
                i.toString(),
                "Speed + $i",
                "aku adalah pelari $i",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/56/Valentino_Rossi_2017.jpg/800px-Valentino_Rossi_2017.jpg",
                "2022-01-08T06:34:18.598Z",
                -10.212,
                -16.002

            )
            storyList.add(story)
        }
        return storyList
    }
}