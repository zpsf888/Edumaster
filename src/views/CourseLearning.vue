<template>
  <div class="course-learning-container">
    <div class="course-header">
      <button class="back-btn" @click="router.back()">
        <i class="fas fa-arrow-left"></i>
        返回
      </button>
      <h1>{{ course.title }}</h1>
      <div class="course-meta">
        <span>课号：{{ course.courseNumber }}</span>
      </div>
    </div>

    <div class="course-content">
      <div class="course-description">
        <h2>课程简介</h2>
        <p>{{ course.description }}</p>
      </div>

      <div class="video-list">
        <h2>课程视频</h2>
        <div v-for="video in course.videos" :key="video.id" class="video-item">
          <div class="video-info">
            <h3>{{ video.title }}</h3>
            <p>{{ video.description }}</p>
          </div>
          <div class="video-actions">
            <button class="play-btn" @click="handlePlayVideo(video)">
              <i class="fas fa-play"></i>
              播放
            </button>
            <button class="comment-btn" @click="handleViewComments(video)">
              <i class="fas fa-comments"></i>
              评论
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 视频播放器模态框 -->
    <div v-if="showVideoPlayer" class="modal-overlay" @click="closeVideoPlayer">
      <div class="modal-content" @click.stop>
        <button class="close-btn" @click="closeVideoPlayer">
          <i class="fas fa-times"></i>
        </button>
        <div class="video-player">
          <video ref="videoPlayer" controls>
            <source :src="currentVideo?.url" type="video/mp4">
            您的浏览器不支持视频播放。
          </video>
        </div>
      </div>
    </div>

    <!-- 评论区模态框 -->
    <div v-if="showComments" class="modal-overlay" @click="closeComments">
      <div class="modal-content comments-modal" @click.stop>
        <button class="close-btn" @click="closeComments">
          <i class="fas fa-times"></i>
        </button>
        <div class="comments-container">
          <h3>评论区</h3>
          <div class="comment-input">
            <textarea 
              v-model="newComment" 
              placeholder="写下你的评论..."
              rows="3"
            ></textarea>
            <button class="submit-comment" @click="submitComment">
              发表评论
            </button>
          </div>
          <div class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-header">
                <span class="comment-author">{{ comment.username }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

interface Video {
  id: number;
  title: string;
  description: string;
  url: string;
}

interface Course {
  courseId: number;
  title: string;
  description: string;
  courseNumber: string;
  videos: Video[];
}

interface CourseResponse {
  code: number;
  data: Course;
  message: string;
}

interface Comment {
  id: number;
  content: string;
  username: string;
  createTime: string;
}

interface CommentResponse {
  code: number;
  data: Comment[];
  message: string;
}

export default defineComponent({
  name: 'CourseLearning',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const course = ref<Course>({
      courseId: 0,
      title: '',
      description: '',
      courseNumber: '',
      videos: []
    })
    const showVideoPlayer = ref(false)
    const showComments = ref(false)
    const currentVideo = ref<Video | null>(null)
    const comments = ref<Comment[]>([])
    const newComment = ref('')
    const videoPlayer = ref<HTMLVideoElement | null>(null)

    const fetchCourseBasicInfo = async () => {
      try {
        const courseId = route.params.id
        const response = await axios.get<CourseResponse>(`http://localhost:8081/courses/${courseId}`)
        
        if (response.data.code === 200) {
          course.value = {
            ...response.data.data,
            courseNumber: `CS${response.data.data.courseId.toString().padStart(3, '0')}`
          }
        } else {
          throw new Error(response.data.message || '获取课程详情失败')
        }
      } catch (error) {
        console.error('获取课程详情时出错：', error)
        alert(error instanceof Error ? error.message : '获取课程详情失败，请稍后重试')
        router.back()
      }
    }

    const handlePlayVideo = (video: Video) => {
      currentVideo.value = video
      showVideoPlayer.value = true
      // 等待DOM更新后播放视频
      setTimeout(() => {
        if (videoPlayer.value) {
          videoPlayer.value.play()
        }
      }, 100)
    }

    const closeVideoPlayer = () => {
      if (videoPlayer.value) {
        videoPlayer.value.pause()
      }
      showVideoPlayer.value = false
      currentVideo.value = null
    }

    const handleViewComments = async (video: Video) => {
      currentVideo.value = video
      showComments.value = true
      await fetchComments(video.id)
    }

    const closeComments = () => {
      showComments.value = false
      currentVideo.value = null
      comments.value = []
      newComment.value = ''
    }

    const fetchComments = async (videoId: number) => {
      try {
        const response = await axios.get<CommentResponse>(`http://localhost:8081/videos/${videoId}/comments`)
        if (response.data.code === 200) {
          comments.value = response.data.data
        } else {
          throw new Error(response.data.message || '获取评论失败')
        }
      } catch (error) {
        console.error('获取评论时出错：', error)
        alert(error instanceof Error ? error.message : '获取评论失败，请稍后重试')
      }
    }

    const submitComment = async () => {
      if (!currentVideo.value || !newComment.value.trim()) return

      try {
        // TODO: 从用户状态获取userId
        const userId = 1 // 临时使用固定值，后续需要从用户状态获取
        const response = await axios.post(`http://localhost:8081/videos/${currentVideo.value.id}/comments`, {
          userId,
          content: newComment.value.trim()
        })

        if (response.data.code === 200) {
          newComment.value = ''
          await fetchComments(currentVideo.value.id)
        } else {
          throw new Error(response.data.message || '发表评论失败')
        }
      } catch (error) {
        console.error('发表评论时出错：', error)
        alert(error instanceof Error ? error.message : '发表评论失败，请稍后重试')
      }
    }

    const formatTime = (time: string) => {
      return new Date(time).toLocaleString()
    }

    onMounted(() => {
      fetchCourseBasicInfo()
    })

    return {
      course,
      router,
      showVideoPlayer,
      showComments,
      currentVideo,
      comments,
      newComment,
      videoPlayer,
      handlePlayVideo,
      handleViewComments,
      closeVideoPlayer,
      closeComments,
      submitComment,
      formatTime
    }
  }
})
</script>

<style scoped>
.course-learning-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.course-header {
  margin-bottom: 2rem;
}

.back-btn {
  background: none;
  border: none;
  color: #4a5568;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0;
  margin-bottom: 1rem;
}

.back-btn:hover {
  color: #2d3748;
}

.course-header h1 {
  color: #2c5282;
  font-size: 2rem;
  margin-bottom: 1rem;
}

.course-meta {
  color: #718096;
  font-size: 1.1rem;
}

.course-content {
  display: grid;
  gap: 2rem;
}

.course-description {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.course-description h2 {
  color: #2c5282;
  margin-bottom: 1rem;
  font-size: 1.5rem;
}

.course-description p {
  color: #4a5568;
  line-height: 1.6;
}

.video-list {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.video-list h2 {
  color: #2c5282;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
}

.video-item {
  padding: 1.5rem;
  border: 1px solid #e6f3ff;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.video-item:last-child {
  margin-bottom: 0;
}

.video-info h3 {
  color: #2d3748;
  margin-bottom: 0.5rem;
  font-size: 1.2rem;
}

.video-info p {
  color: #718096;
  margin-bottom: 1rem;
}

.video-actions {
  display: flex;
  gap: 1rem;
}

.play-btn, .comment-btn {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
}

.play-btn {
  background-color: #4CAF50;
  color: white;
}

.play-btn:hover {
  background-color: #45a049;
}

.comment-btn {
  background-color: #2196F3;
  color: white;
}

.comment-btn:hover {
  background-color: #1976D2;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  position: relative;
  width: 90%;
  max-width: 1000px;
  max-height: 90vh;
  overflow-y: auto;
}

.close-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  color: #4a5568;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0.5rem;
  line-height: 1;
}

.close-btn:hover {
  color: #2d3748;
}

.video-player {
  width: 100%;
  aspect-ratio: 16/9;
  background: black;
  border-radius: 8px;
  overflow: hidden;
}

.video-player video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.comments-modal {
  max-width: 600px;
}

.comments-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.comments-container h3 {
  color: #2c5282;
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.comment-input {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-input textarea {
  width: 100%;
  padding: 1rem;
  border: 1px solid #e6f3ff;
  border-radius: 8px;
  resize: vertical;
  font-size: 1rem;
  line-height: 1.5;
}

.comment-input textarea:focus {
  outline: none;
  border-color: #2196F3;
  box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.1);
}

.submit-comment {
  align-self: flex-end;
  padding: 0.8rem 1.5rem;
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.submit-comment:hover {
  background-color: #1976D2;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-item {
  padding: 1rem;
  border: 1px solid #e6f3ff;
  border-radius: 8px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.comment-author {
  color: #2c5282;
  font-weight: 500;
}

.comment-time {
  color: #718096;
  font-size: 0.9rem;
}

.comment-content {
  color: #4a5568;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .course-learning-container {
    padding: 1rem;
  }

  .course-header h1 {
    font-size: 1.5rem;
  }

  .video-actions {
    flex-direction: column;
  }

  .modal-content {
    width: 95%;
    padding: 1rem;
  }

  .comments-modal {
    max-width: 100%;
  }
}
</style> 