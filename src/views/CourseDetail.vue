<template>
  <div class="course-detail">
    <div v-if="isLoading" class="loading-state">
      <i class="fas fa-spinner fa-spin"></i>
      加载中...
    </div>
    <template v-else>
      <div class="header-actions">
        <div class="back-button" @click="goBack">
          <i class="fas fa-arrow-left"></i>
          返回课程列表
        </div>
        <div class="right-actions">
          <button class="new-lesson-btn" @click="openNewLessonModal">
            <i class="fas fa-plus"></i>
            新建课程
          </button>
          <button class="ai-support-btn" @click="goToAISupport">
            <i class="fas fa-robot"></i>
            AI智能答疑
          </button>
        </div>
      </div>
      <div class="course-header">
        <div class="course-info">
          <div class="title-section">
            <h1>{{ course?.name }}</h1>
            <button class="edit-btn" @click="showEditModal = true">
              <i class="fas fa-edit"></i>
              编辑课程简介
            </button>
          </div>
          <div class="course-meta">
            <span class="course-number">课号：{{ course?.courseNumber }}</span>
          </div>
          <p class="description">{{ course?.description }}</p>
        </div>
      </div>

      <!-- 编辑课程弹窗 -->
      <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h2>编辑课程简介</h2>
            <button class="close-btn" @click="closeEditModal">&times;</button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="submitEdit">
              <div class="form-group">
                <label for="courseName">课程名称</label>
                <input
                  type="text"
                  id="courseName"
                  v-model="editForm.name"
                  placeholder="请输入课程名称"
                  required
                >
              </div>
              <div class="form-group">
                <label for="courseDescription">课程简介</label>
                <textarea
                  id="courseDescription"
                  v-model="editForm.description"
                  placeholder="请输入课程简介"
                  rows="4"
                  required
                ></textarea>
              </div>
              <div class="form-actions">
                <button type="button" class="cancel-btn" @click="closeEditModal">取消</button>
                <button type="submit" class="submit-btn" :disabled="isSubmitting">
                  <i class="fas" :class="isSubmitting ? 'fa-spinner fa-spin' : 'fa-check'"></i>
                  {{ isSubmitting ? '保存中...' : '保存修改' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <div class="course-content">
        <h2>课程目录</h2>
        <div class="video-list">
          <div v-for="video in courseVideos" :key="video.id" class="video-item" 
            :class="{ 'completed': video.completed }"
            @click="playVideo(video)">
            <div class="video-info">
              <i class="fas" :class="video.completed ? 'fa-check-circle' : 'fa-play-circle'"></i>
              <span class="video-title">{{ video.title }}</span>
              <span class="video-duration">{{ video.duration }}</span>
            </div>
            <div class="video-actions">
              <div class="video-progress" v-if="video.completed">
                <span class="progress-text">已完成</span>
              </div>
              <button class="comment-btn" @click.stop="showComments(video)">
                <i class="fas fa-comments"></i>
                查看评论
              </button>
              <button 
                class="delete-btn" 
                @click.stop="handleDeleteLesson(course?.id || 0, video.id)"
                :disabled="isDeleting && deletingLessonId === video.id"
              >
                <i class="fas" :class="isDeleting && deletingLessonId === video.id ? 'fa-spinner fa-spin' : 'fa-trash'"></i>
                {{ isDeleting && deletingLessonId === video.id ? '删除中...' : '删除' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 视频播放弹窗 -->
    <div v-if="showVideoPlayer" class="video-player-modal">
      <div class="video-player-content">
        <div class="video-player-header">
          <h3>{{ currentVideo?.title }}</h3>
          <button class="close-btn" @click="closeVideoPlayer">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="video-player">
          <video 
            ref="videoPlayer"
            :src="currentVideo?.videoUrl" 
            controls 
            class="video-element"
          ></video>
        </div>
      </div>
    </div>

    <!-- 评论区弹窗 -->
    <div v-if="showCommentModal" class="comment-modal">
      <div class="comment-content">
        <div class="comment-header">
          <h3>{{ currentVideo?.title }} - 评论区</h3>
          <button class="close-btn" @click="closeComments">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="comment-list">
          <div v-for="comment in currentVideoComments" :key="comment.id" class="comment-item">
            <div class="comment-user">
              <img :src="comment.avatar" :alt="comment.username" class="user-avatar">
              <span class="username">{{ comment.username }}</span>
              <span class="comment-time">{{ comment.time }}</span>
            </div>
            <p class="comment-text">{{ comment.content }}</p>
            <div class="comment-actions">
              <button class="like-btn" @click="likeComment(comment)">
                <i class="fas" :class="comment.isLiked ? 'fa-heart' : 'fa-heart-o'"></i>
                {{ comment.likes }}
              </button>
              <button class="reply-btn" @click="replyToComment(comment)">
                <i class="fas fa-reply"></i>
                回复
              </button>
            </div>
            <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
              <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <div class="comment-user">
                  <img :src="reply.avatar" :alt="reply.username" class="user-avatar">
                  <span class="username">{{ reply.username }}</span>
                  <span class="comment-time">{{ reply.time }}</span>
                </div>
                <p class="comment-text">{{ reply.content }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="comment-input">
          <textarea 
            v-model="newComment" 
            placeholder="写下你的评论..."
            @keyup.enter="submitComment"
          ></textarea>
          <button class="submit-btn" @click="submitComment">
            发表评论
          </button>
        </div>
      </div>
    </div>

    <!-- 新建课程弹窗 -->
    <div v-if="showNewLessonModal" class="modal-overlay">
      <div class="modal-content">
        <div class="modal-header">
          <h2>新建课程</h2>
          <button class="close-btn" @click="closeNewLessonModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitNewLesson">
            <div class="form-group">
              <label>课号</label>
              <input type="text" :value="course?.courseNumber" disabled class="form-control" />
            </div>
            <div class="form-group">
              <label>小课号 <span class="required">*</span></label>
              <input type="text" v-model="newLessonForm.lessonNumber" class="form-control" placeholder="请输入小课号" />
            </div>
            <div class="form-group">
              <label>小课标题 <span class="required">*</span></label>
              <input type="text" v-model="newLessonForm.title" class="form-control" placeholder="请输入小课标题" />
            </div>
            <div class="form-group">
              <label>小课简介 <span class="required">*</span></label>
              <textarea v-model="newLessonForm.description" class="form-control" rows="4" placeholder="请输入小课简介"></textarea>
            </div>
            <div class="form-group">
              <label>上传文件</label>
              <div class="file-upload">
                <input 
                  type="file" 
                  class="file-input" 
                  ref="fileInput"
                  @change="handleFileSelect"
                />
                <button 
                  type="button" 
                  class="upload-btn"
                  @click="triggerFileInput"
                  :disabled="isUploading"
                >
                  <i class="fas" :class="isUploading ? 'fa-spinner fa-spin' : 'fa-upload'"></i>
                  {{ isUploading ? '上传中...' : '选择文件' }}
                </button>
                <span class="file-name">{{ selectedFileName }}</span>
              </div>
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeNewLessonModal">取消</button>
              <button type="submit" class="submit-btn" :disabled="isSubmittingNewLesson">
                <i class="fas fa-spinner fa-spin" v-if="isSubmittingNewLesson"></i>
                {{ isSubmittingNewLesson ? '提交中...' : '提交' }}
              </button>
            </div>
          </form>
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
  duration: string;
  completed: boolean;
  videoUrl: string;
}

interface CourseLesson {
  courseLessonId: number;
  courseId: number;
  lessonId: number;
  title: string;
  videoUrl: string;
  description: string;
}

interface CourseBasicInfo {
  courseId: number;
  title: string;
  description: string;
  creatorId: number;
}

interface CourseInfoResponse {
  code: number;
  message: string;
  data: CourseBasicInfo;
}

interface CourseDetail {
  id: number;
  name: string;
  description: string;
  courseNumber: string;
  image: string;
}

interface CourseListResponse {
  code: number;
  message: string;
  data: CourseLesson[];
}

interface Comment {
  id: number;
  username: string;
  avatar: string;
  content: string;
  time: string;
  likes: number;
  isLiked: boolean;
  replies?: Comment[];
}

interface EditForm {
  name: string;
  description: string;
}

interface NewLessonForm {
  lessonNumber: string;
  title: string;
  description: string;
  fileUrl: string;
}

interface CreateLessonRequest {
  courseId: number;
  lessonId: number;
  title: string;
  videoUrl: string;
  description: string;
}

interface CreateLessonResponse {
  code: number;
  message: string;
}

interface UploadResponse {
  message: string;
  url: string;
}

interface DeleteLessonResponse {
  code: number;
  message: string;
}

export default defineComponent({
  name: 'CourseDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const course = ref<CourseDetail | null>(null)
    const courseVideos = ref<Video[]>([])
    const showVideoPlayer = ref(false)
    const currentVideo = ref<Video | null>(null)
    const videoPlayer = ref<HTMLVideoElement | null>(null)
    const showCommentModal = ref(false)
    const currentVideoComments = ref<Comment[]>([])
    const newComment = ref('')
    const showEditModal = ref(false)
    const isSubmitting = ref(false)
    const isLoading = ref(true)
    const editForm = ref<EditForm>({
      name: '',
      description: ''
    })
    const showNewLessonModal = ref(false)
    const newLessonForm = ref<NewLessonForm>({
      lessonNumber: '',
      title: '',
      description: '',
      fileUrl: undefined
    })
    const isSubmittingNewLesson = ref(false)
    const fileInput = ref<HTMLInputElement | null>(null)
    const selectedFileName = ref('未选择文件')
    const isUploading = ref(false)
    const isDeleting = ref(false)
    const deletingLessonId = ref<number | null>(null)

    const fetchCourseBasicInfo = async (courseId: number) => {
      try {
        const response = await axios.get<CourseInfoResponse>(`http://localhost:8081/courses/${courseId}`)
        if (response.data.code === 200) {
          const courseInfo = response.data.data
          course.value = {
            id: courseInfo.courseId,
            name: courseInfo.title,
            description: courseInfo.description,
            courseNumber: `CS${courseInfo.courseId.toString().padStart(3, '0')}`,
            image: '/course-images/default.jpg'
          }

          // 初始化编辑表单
          editForm.value = {
            name: course.value.name,
            description: course.value.description
          }
        } else {
          alert('获取课程信息失败：' + response.data.message)
        }
      } catch (error) {
        console.error('获取课程信息时出错：', error)
        alert('获取课程信息失败，请稍后重试')
      }
    }

    const fetchCourseDetails = async () => {
      try {
        isLoading.value = true
        const courseId = Number(route.params.id)
        
        // 先获取课程基本信息
        await fetchCourseBasicInfo(courseId)
        
        // 获取课程课节信息
        const response = await axios.get<CourseListResponse>(`http://localhost:8081/course-lessons/course/${courseId}`)
        
        if (response.data.code === 200) {
          // 转换课程视频数据
          courseVideos.value = response.data.data.map(lesson => ({
            id: lesson.courseLessonId,
            title: lesson.title,
            duration: '00:00', // 这里可以后续添加视频时长
            completed: false, // 这里可以后续添加完成状态
            videoUrl: lesson.videoUrl
          }))
        } else if (response.data.code === 404 && response.data.message === '该课程暂无章节信息') {
          // 如果是404且消息为"该课程暂无章节信息"，不显示错误提示
          courseVideos.value = []
        } else {
          alert('获取课程详情失败：' + response.data.message)
        }
      } catch (error) {
        console.error('获取课程详情时出错：', error)
        alert('获取课程详情失败，请稍后重试')
      } finally {
        isLoading.value = false
      }
    }

    onMounted(() => {
      fetchCourseDetails()
    })

    const goBack = () => {
      router.back()
    }

    const goToAISupport = () => {
      router.push({
        name: 'AISupport',
        params: { id: route.params.id }
      })
    }

    const closeEditModal = () => {
      showEditModal.value = false
      if (course.value) {
        editForm.value = {
          name: course.value.name,
          description: course.value.description
        }
      }
      isSubmitting.value = false
    }

    const submitEdit = async () => {
      try {
        isSubmitting.value = true
        const courseId = Number(route.params.id)
        
        const response = await axios.put(`http://localhost:8081/courses/${courseId}`, {
          title: editForm.value.name,
          description: editForm.value.description,
          creatorId: 1  // 这里应该使用实际的用户ID
        })

        if (response.data.code === 200) {
          if (course.value) {
            course.value.name = editForm.value.name
            course.value.description = editForm.value.description
          }
          alert('课程信息更新成功！')
          closeEditModal()
        } else {
          alert('更新失败：' + response.data.message)
        }
      } catch (error) {
        console.error('更新课程信息时出错：', error)
        alert('更新失败，请稍后重试')
      } finally {
        isSubmitting.value = false
      }
    }

    const playVideo = (video: Video) => {
      currentVideo.value = video
      showVideoPlayer.value = true
    }

    const closeVideoPlayer = () => {
      showVideoPlayer.value = false
      if (videoPlayer.value) {
        videoPlayer.value.pause()
      }
    }

    const showComments = (video: Video) => {
      currentVideo.value = video
      showCommentModal.value = true
      // 模拟加载评论数据
      currentVideoComments.value = [
        {
          id: 1,
          username: '学习达人',
          avatar: '/avatars/user1.jpg',
          content: '这节课讲得很好，概念讲解得特别清晰！',
          time: '2024-01-20 14:30',
          likes: 12,
          isLiked: false,
          replies: [
            {
              id: 11,
              username: '前端新手',
              avatar: '/avatars/user2.jpg',
              content: '同意，特别是关于组件化的部分讲解很透彻',
              time: '2024-01-20 15:00',
              likes: 3,
              isLiked: false
            }
          ]
        },
        {
          id: 2,
          username: 'Vue爱好者',
          avatar: '/avatars/user3.jpg',
          content: '老师讲得很专业，但是希望能多一些实战案例',
          time: '2024-01-20 16:45',
          likes: 8,
          isLiked: false
        }
      ]
    }

    const closeComments = () => {
      showCommentModal.value = false
      newComment.value = ''
    }

    const submitComment = () => {
      if (!newComment.value.trim()) return
      
      const comment: Comment = {
        id: Date.now(),
        username: '当前用户',
        avatar: '/avatars/default.jpg',
        content: newComment.value,
        time: new Date().toLocaleString(),
        likes: 0,
        isLiked: false
      }
      
      currentVideoComments.value.unshift(comment)
      newComment.value = ''
    }

    const likeComment = (comment: Comment) => {
      comment.isLiked = !comment.isLiked
      comment.likes += comment.isLiked ? 1 : -1
    }

    const replyToComment = (comment: Comment) => {
      newComment.value = `@${comment.username} `
    }

    const openNewLessonModal = () => {
      showNewLessonModal.value = true
    }

    const closeNewLessonModal = () => {
      showNewLessonModal.value = false
      newLessonForm.value = {
        lessonNumber: '',
        title: '',
        description: '',
        fileUrl: undefined
      }
      selectedFileName.value = '未选择文件'
    }

    const submitNewLesson = async () => {
      if (!newLessonForm.value.lessonNumber || !newLessonForm.value.title || !newLessonForm.value.description) {
        alert('请填写所有必填字段')
        return
      }

      if (!newLessonForm.value.fileUrl) {
        alert('请上传课程文件')
        return
      }

      try {
        isSubmittingNewLesson.value = true
        const courseId = Number(route.params.id)
        
        const requestData: CreateLessonRequest = {
          courseId: courseId,
          lessonId: Number(newLessonForm.value.lessonNumber),
          title: newLessonForm.value.title,
          videoUrl: newLessonForm.value.fileUrl,
          description: newLessonForm.value.description
        }

        const response = await axios.post<CreateLessonResponse>(
          'http://localhost:8081/course-lessons',
          requestData
        )

        if (response.data.code === 201) {
          alert('课程课节创建成功')
          closeNewLessonModal()
          // 刷新课程列表
          await fetchCourseDetails()
        } else {
          throw new Error(response.data.message || '创建失败')
        }
      } catch (error) {
        console.error('创建课程课节时出错：', error)
        alert(error instanceof Error ? error.message : '创建课程课节失败，请稍后重试')
      } finally {
        isSubmittingNewLesson.value = false
      }
    }

    const handleFileSelect = async (event: Event) => {
      const input = event.target as HTMLInputElement
      if (!input.files || input.files.length === 0) return

      const file = input.files[0]
      selectedFileName.value = file.name
      isUploading.value = true

      try {
        const formData = new FormData()
        formData.append('file', file)

        const response = await axios.post<UploadResponse>('http://localhost:8081/uploadFile', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

        if (response.data.url) {
          newLessonForm.value.fileUrl = response.data.url
          alert('文件上传成功')
        } else {
          throw new Error('上传失败：未获取到文件URL')
        }
      } catch (error) {
        console.error('文件上传失败：', error)
        alert('文件上传失败，请重试')
        selectedFileName.value = '未选择文件'
        newLessonForm.value.fileUrl = undefined
      } finally {
        isUploading.value = false
      }
    }

    const triggerFileInput = () => {
      fileInput.value?.click()
    }

    const handleDeleteLesson = async (courseId: number, lessonId: number) => {
      if (!confirm('确定要删除这节课程吗？')) {
        return
      }

      try {
        isDeleting.value = true
        deletingLessonId.value = lessonId

        const response = await axios.delete<DeleteLessonResponse>(
          `http://localhost:8081/course-lessons/${courseId}/${lessonId}`
        )

        if (response.data.code === 200) {
          alert('课程课节删除成功')
          // 刷新课程列表
          await fetchCourseDetails()
        } else {
          throw new Error(response.data.message || '删除失败')
        }
      } catch (error) {
        console.error('删除课程课节时出错：', error)
        alert(error instanceof Error ? error.message : '删除课程课节失败，请稍后重试')
      } finally {
        isDeleting.value = false
        deletingLessonId.value = null
      }
    }

    return {
      course,
      courseVideos,
      showVideoPlayer,
      currentVideo,
      videoPlayer,
      playVideo,
      closeVideoPlayer,
      showCommentModal,
      currentVideoComments,
      newComment,
      showComments,
      closeComments,
      submitComment,
      likeComment,
      replyToComment,
      goBack,
      goToAISupport,
      showEditModal,
      editForm,
      isSubmitting,
      closeEditModal,
      submitEdit,
      isLoading,
      showNewLessonModal,
      newLessonForm,
      isSubmittingNewLesson,
      openNewLessonModal,
      closeNewLessonModal,
      submitNewLesson,
      fileInput,
      selectedFileName,
      isUploading,
      handleFileSelect,
      triggerFileInput,
      isDeleting,
      deletingLessonId,
      handleDeleteLesson
    }
  }
})
</script>

<style scoped>
.course-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.2rem;
  background-color: #f7fafc;
  border-radius: 8px;
  color: #3182ce;
  cursor: pointer;
  width: fit-content;
  margin-bottom: 1.5rem;
  transition: all 0.3s ease;
  font-weight: 500;
}

.back-button:hover {
  background-color: #e6f3ff;
  transform: translateX(-5px);
}

.back-button i {
  font-size: 1.1rem;
}

.right-actions {
  display: flex;
  gap: 1rem;
}

.new-lesson-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s;
}

.new-lesson-btn:hover {
  background-color: #45a049;
}

.ai-support-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.8rem 1.2rem;
  background-color: #3182ce;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.ai-support-btn:hover {
  background-color: #2c5282;
  transform: translateY(-2px);
}

.ai-support-btn i {
  font-size: 1.1rem;
}

.course-header {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.course-info h1 {
  font-size: 2rem;
  color: #2c5282;
  margin-bottom: 1rem;
}

.instructor {
  color: #4a5568;
  font-size: 1.1rem;
  margin-bottom: 1rem;
}

.description {
  color: #718096;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.meta {
  display: flex;
  gap: 2rem;
  color: #4a5568;
}

.meta span {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.meta i {
  color: #3182ce;
}

.course-content {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.course-content h2 {
  color: #2c5282;
  margin-bottom: 1.5rem;
}

.video-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.video-item {
  padding: 1rem;
  border-radius: 8px;
  background-color: #f7fafc;
  cursor: pointer;
  transition: all 0.3s ease;
}

.video-item:hover {
  background-color: #e6f3ff;
  transform: translateX(5px);
}

.video-item.completed {
  background-color: #f0fff4;
}

.video-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.video-info i {
  color: #3182ce;
  font-size: 1.2rem;
}

.video-item.completed i {
  color: #48bb78;
}

.video-title {
  flex: 1;
  color: #2d3748;
}

.video-duration {
  color: #718096;
}

.video-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.video-progress {
  margin-top: 0.5rem;
  color: #48bb78;
  font-size: 0.9rem;
}

.comment-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  background-color: #e6f3ff;
  color: #3182ce;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.comment-btn:hover {
  background-color: #3182ce;
  color: white;
}

.video-player-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.video-player-content {
  background-color: #ffffff;
  border-radius: 12px;
  width: 90%;
  max-width: 1000px;
  overflow: hidden;
}

.video-player-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  border-bottom: 1px solid #e2e8f0;
}

.video-player-header h3 {
  color: #2c5282;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  color: #718096;
  cursor: pointer;
  font-size: 1.5rem;
  padding: 0.5rem;
}

.close-btn:hover {
  color: #2c5282;
}

.video-player {
  padding: 1rem;
}

.video-element {
  width: 100%;
  aspect-ratio: 16/9;
  background-color: #000000;
}

.comment-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.comment-content {
  background-color: #ffffff;
  border-radius: 12px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  border-bottom: 1px solid #e2e8f0;
}

.comment-list {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
}

.comment-item {
  margin-bottom: 1.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.username {
  font-weight: 500;
  color: #2d3748;
}

.comment-time {
  color: #718096;
  font-size: 0.9rem;
}

.comment-text {
  color: #4a5568;
  line-height: 1.6;
  margin: 0.5rem 0;
}

.comment-actions {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
}

.like-btn, .reply-btn {
  background: none;
  border: none;
  color: #718096;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.like-btn:hover, .reply-btn:hover {
  color: #3182ce;
}

.reply-list {
  margin-left: 3rem;
  margin-top: 1rem;
  padding-left: 1rem;
  border-left: 2px solid #e2e8f0;
}

.reply-item {
  margin-bottom: 1rem;
}

.comment-input {
  padding: 1.5rem;
  border-top: 1px solid #e2e8f0;
  background-color: #f7fafc;
}

.comment-input textarea {
  width: 100%;
  height: 80px;
  padding: 0.8rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  resize: none;
  margin-bottom: 1rem;
}

.submit-btn {
  padding: 0.8rem 2rem;
  background-color: #3182ce;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background-color: #2c5282;
}

.title-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.edit-btn {
  padding: 0.6rem 1.2rem;
  background-color: #2196F3;
  color: white;
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

.edit-btn:hover {
  background-color: #1976D2;
  transform: translateY(-2px);
}

.edit-btn i {
  font-size: 1rem;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #e6f3ff;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
  color: #2c5282;
  font-size: 1.5rem;
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #4a5568;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #e6f3ff;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #2196F3;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.cancel-btn,
.submit-btn {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.cancel-btn {
  background-color: #e2e8f0;
  color: #4a5568;
}

.cancel-btn:hover {
  background-color: #cbd5e0;
}

.submit-btn {
  background-color: #2196F3;
  color: white;
}

.submit-btn:hover:not(:disabled) {
  background-color: #1976D2;
}

.submit-btn:disabled {
  background-color: #90caf9;
  cursor: not-allowed;
}

.submit-btn i {
  margin-right: 0.5rem;
}

@media (max-width: 768px) {
  .course-detail {
    padding: 1rem;
  }

  .meta {
    flex-direction: column;
    gap: 1rem;
  }

  .video-player-content {
    width: 95%;
    margin: 1rem;
  }

  .comment-content {
    width: 95%;
    max-height: 95vh;
  }

  .comment-list {
    padding: 1rem;
  }

  .reply-list {
    margin-left: 1.5rem;
  }
}

.course-meta {
  display: flex;
  gap: 2rem;
  color: #4a5568;
  margin-bottom: 1.5rem;
}

.course-number {
  font-size: 1.1rem;
  font-weight: 500;
  color: #2c5282;
  padding: 0.5rem 0;
}

.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  color: #4a5568;
  font-size: 1.2rem;
}

.loading-state i {
  margin-right: 0.5rem;
  font-size: 1.5rem;
}

.required {
  color: #e53e3e;
}

.form-control {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 0.9rem;
}

.form-control:disabled {
  background-color: #f7fafc;
  cursor: not-allowed;
}

.file-upload {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.file-input {
  display: none;
}

.upload-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #4a5568;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s;
  min-width: 100px;
  justify-content: center;
}

.upload-btn:hover:not(:disabled) {
  background-color: #2d3748;
}

.upload-btn:disabled {
  background-color: #a0aec0;
  cursor: not-allowed;
}

.file-name {
  color: #718096;
  font-size: 0.9rem;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1.5rem;
}

.cancel-btn {
  padding: 0.5rem 1rem;
  background-color: #e2e8f0;
  color: #4a5568;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s;
}

.cancel-btn:hover {
  background-color: #cbd5e0;
}

.submit-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #4299e1;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background-color: #3182ce;
}

.submit-btn:disabled {
  background-color: #a0aec0;
  cursor: not-allowed;
}

.video-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.delete-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #e53e3e;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.delete-btn:hover:not(:disabled) {
  background-color: #c53030;
}

.delete-btn:disabled {
  background-color: #a0aec0;
  cursor: not-allowed;
}
</style> 