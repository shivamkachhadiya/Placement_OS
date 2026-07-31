const API_BASE = "http://localhost:8080";
let token = localStorage.getItem("token");
let currentUser = JSON.parse(localStorage.getItem("user") || "null");
let isRegisterMode = false;
let currentFeedMode = 'ALL';
let activeCompanyFilter = 'ALL';
let rawPostsData = [];

// Local state store for posts
const postInteractions = {};

document.addEventListener("DOMContentLoaded", () => {
    updateAuthUI();
    loadFeed();

    const contentInput = document.getElementById("postContent");
    if (contentInput) {
        contentInput.addEventListener("input", (e) => {
            document.getElementById("charCounter").innerText = `${e.target.value.length} / 5000 chars`;
        });
    }
});

// Modals Handlers
function showSuccessModal(msg) {
    if (msg) document.getElementById("successModalMessage").innerText = msg;
    document.getElementById("successModal").classList.remove("hidden");
}

function closeSuccessModal() {
    document.getElementById("successModal").classList.add("hidden");
}

function showCustomAlert(message) {
    const msgElement = document.getElementById("customAlertMessage");
    const modal = document.getElementById("customAlertModal");
    if (msgElement) msgElement.innerText = message;
    if (modal) modal.classList.remove("hidden");
}

function closeCustomAlert() {
    const modal = document.getElementById("customAlertModal");
    if (modal) modal.classList.add("hidden");
}

// User Profile Modal Handlers (BULLETPROOF FIX)
function openProfileModal() {
    const user = currentUser || JSON.parse(localStorage.getItem("user") || "{}");

    const email = user.email || "user@placementos.com";
    const displayName = user.name || (email.includes("@") ? email.split("@")[0] : "User");
    const branch = user.branch || "CSE";
    const batch = user.batch || "2026";
    const role = user.role || "STUDENT";
    const isPlaced = user.placed || false;

    const nameEl = document.getElementById("profileName");
    const emailEl = document.getElementById("profileEmail");
    const branchEl = document.getElementById("profileBranch");
    const batchEl = document.getElementById("profileBatch");
    const roleEl = document.getElementById("profileRole");
    const avatarEl = document.getElementById("profileAvatar");
    const badgeContainer = document.getElementById("profileBadge");

    if (nameEl) nameEl.innerText = displayName;
    if (emailEl) emailEl.innerText = email;
    if (branchEl) branchEl.innerText = branch;
    if (batchEl) batchEl.innerText = batch;
    if (roleEl) roleEl.innerText = role;
    if (avatarEl) avatarEl.innerText = displayName.charAt(0).toUpperCase();

    if (badgeContainer) {
        if (isPlaced) {
            badgeContainer.innerHTML = `<span class="px-3 py-1 text-xs rounded-full placed-badge font-bold inline-block">VERIFIED PLACED 🟢</span>`;
        } else {
            badgeContainer.innerHTML = `<span class="px-3 py-1 text-xs rounded-full bg-slate-800 text-slate-400 border border-slate-700 font-medium inline-block">UNPLACED / SEARCHING</span>`;
        }
    }

    const modal = document.getElementById("profileModal");
    if (modal) {
        modal.classList.remove("hidden");
    }
}

function closeProfileModal() {
    const modal = document.getElementById("profileModal");
    if (modal) modal.classList.add("hidden");
}

// Owner Modal Handlers
function openOwnerModal() {
    const modal = document.getElementById("ownerModal");
    if (modal) modal.classList.remove("hidden");
}

function closeOwnerModal() {
    const modal = document.getElementById("ownerModal");
    if (modal) modal.classList.add("hidden");
}

function updateAuthUI() {
    const container = document.getElementById("authActions");
    const feedTabs = document.getElementById("feedTabs");
    if (!container) return;

    if (token && currentUser) {
        if (feedTabs) feedTabs.classList.remove("hidden");
        const displayName = currentUser.name || (currentUser.email ? currentUser.email.split("@")[0] : 'User');

        container.innerHTML = `
            <div class="flex items-center gap-3">
                <button type="button" onclick="openProfileModal()" class="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-slate-900/80 border border-cyber-border hover:border-cyan-500/50 transition-all cursor-pointer">
                    <span class="w-6 h-6 rounded-full bg-cyan-500/20 text-cyan-400 font-bold text-xs flex items-center justify-center">
                        ${displayName.charAt(0).toUpperCase()}
                    </span>
                    <span class="text-sm font-semibold text-slate-200 hover:text-cyan-400 transition-colors">
                        ${displayName}
                    </span>
                    ${currentUser.placed ? '<span class="text-[10px] px-1.5 py-0.5 rounded-full placed-badge font-bold">PLACED 🟢</span>' : ''}
                </button>
                <button type="button" onclick="handleLogout()" class="text-xs bg-red-500/10 text-red-400 border border-red-500/30 px-3 py-1.5 rounded-lg hover:bg-red-500/20 transition-all cursor-pointer">
                    Logout
                </button>
            </div>
        `;
    } else {
        if (feedTabs) feedTabs.classList.add("hidden");
        container.innerHTML = `
            <button type="button" onclick="openAuthModal()" class="btn-primary">
                Login / Register
            </button>
        `;
    }
}

// Micro Skeleton Loading Effect
function renderSkeletonLoader() {
    const feed = document.getElementById("postsFeed");
    if (!feed) return;

    feed.innerHTML = [1, 2].map(() => `
        <div class="glass-card p-6 rounded-2xl border border-cyber-border animate-pulse space-y-4">
            <div class="flex justify-between items-center">
                <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-slate-800"></div>
                    <div class="space-y-2">
                        <div class="h-4 w-32 bg-slate-800 rounded"></div>
                        <div class="h-3 w-20 bg-slate-800/60 rounded"></div>
                    </div>
                </div>
                <div class="h-6 w-24 bg-slate-800 rounded-full"></div>
            </div>
            <div class="h-5 w-3/4 bg-slate-800 rounded mb-2"></div>
            <div class="space-y-2">
                <div class="h-4 w-full bg-slate-800/60 rounded"></div>
                <div class="h-4 w-5/6 bg-slate-800/60 rounded"></div>
            </div>
            <div class="pt-4 border-t border-cyber-border/40 flex gap-6">
                <div class="h-6 w-20 bg-slate-800 rounded"></div>
                <div class="h-6 w-24 bg-slate-800 rounded"></div>
            </div>
        </div>
    `).join("");
}

function reloadCurrentFeed() {
    filterFeed(currentFeedMode);
}

async function filterFeed(mode) {
    currentFeedMode = mode;
    activeCompanyFilter = 'ALL';
    const tabAll = document.getElementById("tabAll");
    const tabMy = document.getElementById("tabMy");
    const feedTitle = document.getElementById("feedTitle");
    const writingPadCard = document.getElementById("writingPadCard");

    renderSkeletonLoader();

    if (mode === 'MY') {
        if (!token || !currentUser) return openAuthModal();

        if (writingPadCard) writingPadCard.classList.add("hidden");
        if (tabAll) tabAll.className = "px-3 py-1.5 text-xs font-semibold rounded-lg text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-all";
        if (tabMy) tabMy.className = "px-3 py-1.5 text-xs font-semibold rounded-lg bg-cyan-500/20 text-cyan-300 border border-cyan-500/30";
        if (feedTitle) feedTitle.innerHTML = `<i class="fa-solid fa-user-pen text-cyan-400"></i> My Experiences Feed`;

        try {
            const res = await fetch(`${API_BASE}/posts`);
            const result = await res.json();
            if (result.success) {
                const userEmail = currentUser.email;
                rawPostsData = (result.data || []).filter(p => p.user && (p.user.email === userEmail || p.user.id === currentUser.id));
                buildCompanyFilterBar(rawPostsData);
                await processAndRenderPosts(rawPostsData, true);
            }
        } catch (err) {
            console.error("Error fetching user posts:", err);
        }
    } else {
        if (writingPadCard) writingPadCard.classList.remove("hidden");
        if (tabAll) tabAll.className = "px-3 py-1.5 text-xs font-semibold rounded-lg bg-cyan-500/20 text-cyan-300 border border-cyan-500/30";
        if (tabMy) tabMy.className = "px-3 py-1.5 text-xs font-semibold rounded-lg text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-all";
        if (feedTitle) feedTitle.innerHTML = `<i class="fa-solid fa-fire text-amber-400"></i> Global Experience Feed`;
        loadFeed();
    }
}

async function loadFeed() {
    renderSkeletonLoader();
    try {
        const res = await fetch(`${API_BASE}/posts`);
        const result = await res.json();
        if (result.success) {
            rawPostsData = result.data || [];
            buildCompanyFilterBar(rawPostsData);
            await processAndRenderPosts(rawPostsData, false);
        }
    } catch (err) {
        console.error("Failed to fetch feed:", err);
    }
}

function buildCompanyFilterBar(posts) {
    const bar = document.getElementById("companyFilterBar");
    if (!bar) return;

    const companies = ['ALL'];
    posts.forEach(p => {
        if (p.companyName) {
            const compUpper = p.companyName.trim().toUpperCase();
            if (!companies.includes(compUpper)) companies.push(compUpper);
        }
    });

    bar.innerHTML = companies.map(c => `
        <button onclick="applyCompanyFilter('${c}')" class="px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${activeCompanyFilter === c ? 'bg-cyan-500 text-black shadow-lg shadow-cyan-500/20 font-bold' : 'bg-slate-900/80 text-slate-300 border border-cyber-border hover:border-cyan-500/40'}">
            ${c === 'ALL' ? '🏢 All Companies' : c}
        </button>
    `).join("");
}

function applyCompanyFilter(companyName) {
    activeCompanyFilter = companyName;
    buildCompanyFilterBar(rawPostsData);

    const isMyPosts = currentFeedMode === 'MY';
    let filtered = rawPostsData;

    if (companyName !== 'ALL') {
        filtered = rawPostsData.filter(p => p.companyName && p.companyName.trim().toUpperCase() === companyName);
    }

    processAndRenderPosts(filtered, isMyPosts);
}

async function processAndRenderPosts(posts, isMyPostsOnly) {
    if (!posts || posts.length === 0) {
        renderPosts([], isMyPostsOnly);
        return;
    }

    const enrichedPosts = await Promise.all(posts.map(async (post) => {
        try {
            const headers = token ? { "Authorization": `Bearer ${token}` } : {};

            const likeRes = await fetch(`${API_BASE}/posts/${post.id}/like-details`, { headers });
            const likeData = await likeRes.json();
            const likesCount = likeData.success ? likeData.data.likesCount : 0;
            const isLiked = likeData.success ? likeData.data.isLiked : false;

            const commRes = await fetch(`${API_BASE}/posts/${post.id}/comments`);
            const commData = await commRes.json();
            const commentsList = commData.success ? (commData.data || []) : [];

            return { ...post, likesCount, isLiked, commentsList };
        } catch (e) {
            return { ...post, likesCount: 0, isLiked: false, commentsList: [] };
        }
    }));

    enrichedPosts.sort((a, b) => b.likesCount - a.likesCount);
    renderPosts(enrichedPosts, isMyPostsOnly);
}

function renderPosts(posts, isMyPostsOnly) {
    const feed = document.getElementById("postsFeed");
    if (!feed) return;
    feed.innerHTML = "";

    if (!posts || posts.length === 0) {
        if (isMyPostsOnly) {
            feed.innerHTML = `
                <div class="glass-card p-10 rounded-2xl text-center border border-cyber-border space-y-3">
                    <i class="fa-solid fa-folder-open text-4xl text-cyan-400/50"></i>
                    <h4 class="text-lg font-bold text-slate-200">No Posts Found</h4>
                    <p class="text-sm text-slate-400">No interview experience matched for this filter.</p>
                </div>
            `;
        } else {
            feed.innerHTML = `<div class="text-center py-10 text-slate-500">No interview experiences found for this filter.</div>`;
        }
        return;
    }

    posts.forEach(post => {
        if (!postInteractions[post.id]) {
            postInteractions[post.id] = {
                allComments: post.commentsList || [],
                isExpanded: false,
                likesCount: post.likesCount || 0,
                isLiked: post.isLiked || false
            };
        } else {
            postInteractions[post.id].allComments = post.commentsList || [];
        }

        const formattedDate = formatTimestamp(post.createdAt);
        const commentCount = postInteractions[post.id].allComments.length;

        const postCard = document.createElement("div");
        postCard.className = "glass-card p-6 rounded-2xl border border-cyber-border hover:border-slate-700 transition-all duration-300 shadow-xl relative";

        postCard.innerHTML = `
            <div class="flex justify-between items-start mb-4">
                <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-slate-800 border border-cyan-500/30 flex items-center justify-center font-bold text-cyan-400">
                        ${post.user && post.user.name ? post.user.name.charAt(0).toUpperCase() : 'U'}
                    </div>
                    <div>
                        <div class="flex items-center gap-2">
                            <h4 class="font-semibold text-slate-200">${post.user ? post.user.name : 'Anonymous'}</h4>
                            ${post.user && post.user.placed ? '<span class="text-[10px] px-2 py-0.5 rounded-full placed-badge font-bold">PLACED 🟢</span>' : ''}
                        </div>
                        <div class="flex items-center gap-2 text-xs text-slate-400">
                            <span>${post.user && post.user.branch ? post.user.branch : ''} • ${post.user && post.user.batch ? post.user.batch : ''}</span>
                            <span>•</span>
                            <span class="text-slate-500"><i class="fa-regular fa-clock mr-1"></i>${formattedDate}</span>
                        </div>
                    </div>
                </div>

                <div class="flex items-center gap-2">
                    <span class="px-3 py-1 rounded-full text-xs font-semibold bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
                        ${post.companyName || 'Company'}
                    </span>
                    ${isMyPostsOnly ? `
                        <button onclick="openEditModal(${post.id}, \`${escapeQuotes(post.companyName)}\`, \`${escapeQuotes(post.title)}\`, \`${escapeQuotes(post.content)}\`)" title="Edit Post" class="p-1.5 rounded-lg bg-cyan-500/10 text-cyan-400 border border-cyan-500/20 hover:bg-cyan-500/20 transition-all">
                            <i class="fa-solid fa-pen-to-square text-xs"></i>
                        </button>
                        <button onclick="handleDeletePost(${post.id})" title="Delete Post" class="p-1.5 rounded-lg bg-red-500/10 text-red-400 border border-red-500/20 hover:bg-red-500/20 transition-all">
                            <i class="fa-solid fa-trash-can text-xs"></i>
                        </button>
                    ` : ''}
                </div>
            </div>

            <h3 class="text-lg font-bold text-slate-100 mb-2">${post.title}</h3>
            <p class="text-slate-300 text-sm whitespace-pre-line leading-relaxed mb-4">${post.content}</p>

            <div class="flex items-center gap-6 pt-4 border-t border-cyber-border/60 text-slate-400 text-sm">
                <button id="upvoteBtn-${post.id}" onclick="handleLike(${post.id})" class="flex items-center gap-2 transition-all font-medium cursor-pointer">
                    <i id="upvoteIcon-${post.id}" class="fa-regular fa-thumbs-up"></i> 
                    <span id="upvoteText-${post.id}">Upvote</span> 
                    <span id="upvoteCount-${post.id}" class="ml-1 px-2 py-0.5 rounded-full text-xs bg-slate-800 border border-slate-700">${post.likesCount || 0}</span>
                </button>

                <button onclick="toggleComments(${post.id})" class="flex items-center gap-2 hover:text-cyan-400 transition-colors font-medium cursor-pointer">
                    <i class="fa-regular fa-comment"></i> 
                    <span>Comments</span>
                    <span id="commentBadge-${post.id}" class="px-2 py-0.5 rounded-full text-xs bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">${commentCount}</span>
                </button>
            </div>

            <div id="commentsSection-${post.id}" class="hidden mt-4 pt-4 border-t border-cyber-border/40 space-y-3">
                <div class="flex gap-2">
                    <input type="text" id="commentInput-${post.id}" placeholder="Ask a question..." class="cyber-input text-xs w-full py-2">
                    <button onclick="handleAddComment(${post.id})" class="btn-primary text-xs px-3">Reply</button>
                </div>
                <div id="commentsContainer-${post.id}" class="space-y-2 mt-3"></div>
            </div>
        `;
        feed.appendChild(postCard);

        updateLikeUI(post.id, post.likesCount, post.isLiked);
    });
}

function escapeQuotes(str) {
    if (!str) return '';
    return str.replace(/'/g, "\\'").replace(/"/g, '&quot;').replace(/\n/g, '\\n');
}

function formatTimestamp(isoString) {
    if (!isoString) return "Recently";
    const date = new Date(isoString);
    return date.toLocaleString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
}

function openEditModal(id, company, title, content) {
    document.getElementById("editPostId").value = id;
    document.getElementById("editCompany").value = company;
    document.getElementById("editTitle").value = title;
    document.getElementById("editContent").value = content;
    document.getElementById("editPostModal").classList.remove("hidden");
}

function closeEditModal() { document.getElementById("editPostModal").classList.add("hidden"); }

async function handleUpdatePost() {
    if (!token) return openAuthModal();
    const id = document.getElementById("editPostId").value;
    const companyName = document.getElementById("editCompany").value;
    const title = document.getElementById("editTitle").value;
    const content = document.getElementById("editContent").value;

    try {
        const res = await fetch(`${API_BASE}/posts/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ companyName, title, content })
        });
        const data = await res.json();
        if (data.success) {
            closeEditModal();
            showSuccessModal("Post updated successfully!");
            reloadCurrentFeed();
        } else {
            showCustomAlert(data.message || "Failed to update post!");
        }
    } catch (e) {
        showCustomAlert("Error updating post!");
    }
}

async function handleDeletePost(postId) {
    if (!token) return openAuthModal();
    if (!confirm("Are you sure you want to delete this post?")) return;

    try {
        const res = await fetch(`${API_BASE}/posts/${postId}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        });
        const data = await res.json();

        if (data.success) {
            showSuccessModal("Post deleted successfully!");
            reloadCurrentFeed();
        } else {
            showCustomAlert(data.message || "Could not delete post.");
        }
    } catch (err) {
        showCustomAlert("Failed to delete post.");
    }
}

function updateLikeUI(postId, count, isLiked) {
    const btn = document.getElementById(`upvoteBtn-${postId}`);
    const icon = document.getElementById(`upvoteIcon-${postId}`);
    const text = document.getElementById(`upvoteText-${postId}`);
    const countSpan = document.getElementById(`upvoteCount-${postId}`);

    if (!btn || !postInteractions[postId]) return;

    postInteractions[postId].likesCount = count;
    postInteractions[postId].isLiked = isLiked;

    if (countSpan) countSpan.innerText = count;

    if (isLiked) {
        btn.className = "flex items-center gap-2 text-cyan-400 font-bold transition-all cursor-pointer";
        if (icon) icon.className = "fa-solid fa-thumbs-up text-cyan-400";
        if (text) text.innerText = "Upvoted";
    } else {
        btn.className = "flex items-center gap-2 text-slate-400 hover:text-cyan-400 font-medium transition-all cursor-pointer";
        if (icon) icon.className = "fa-regular fa-thumbs-up";
        if (text) text.innerText = "Upvote";
    }
}

async function handleLike(postId) {
    if (!token) return openAuthModal();

    try {
        const res = await fetch(`${API_BASE}/posts/${postId}/like`, {
            method: "POST",
            headers: { "Authorization": `Bearer ${token}` }
        });
        const result = await res.json();

        if (result.success) {
            updateLikeUI(postId, result.data.likesCount, result.data.isLiked);
            reloadCurrentFeed();
        }
    } catch (err) {
        console.error("Error toggling like:", err);
    }
}

async function toggleComments(postId) {
    const section = document.getElementById(`commentsSection-${postId}`);
    if (!section) return;

    section.classList.toggle("hidden");

    if (!section.classList.contains("hidden")) {
        await loadAndRenderComments(postId);
    }
}

async function loadAndRenderComments(postId) {
    try {
        const res = await fetch(`${API_BASE}/posts/${postId}/comments`);
        const data = await res.json();

        if (data.success) {
            postInteractions[postId].allComments = data.data || [];

            const badge = document.getElementById(`commentBadge-${postId}`);
            if (badge) badge.innerText = postInteractions[postId].allComments.length;

            renderCommentsUI(postId);
        }
    } catch (err) {
        console.error("Error loading comments:", err);
    }
}

function renderCommentsUI(postId) {
    const container = document.getElementById(`commentsContainer-${postId}`);
    if (!container || !postInteractions[postId]) return;

    const { allComments, isExpanded } = postInteractions[postId];

    if (allComments.length === 0) {
        container.innerHTML = `<p class="text-xs text-slate-500 italic py-1">No comments yet. Be the first to reply!</p>`;
        return;
    }

    const visibleComments = isExpanded ? allComments : allComments.slice(0, 2);

    let html = visibleComments.map(c => `
        <div class="bg-slate-900/60 p-2.5 rounded-lg border border-slate-800 text-xs flex justify-between items-center">
            <div>
                <span class="font-bold text-cyan-400">${c.user ? c.user.name : 'User'}:</span> 
                <span class="text-slate-300 ml-1">${c.content}</span>
            </div>
            ${(currentUser && c.user && c.user.email === currentUser.email) ?
        `<button onclick="handleDeleteComment(${c.id}, ${postId})" class="text-red-400 hover:text-red-300 text-[10px] ml-2 cursor-pointer"><i class="fa-solid fa-trash"></i></button>` : ''}
        </div>
    `).join("");

    if (allComments.length > 2) {
        const remaining = allComments.length - 2;
        html += `
            <div class="pt-1 text-center">
                <button onclick="toggleExpandComments(${postId})" class="text-xs text-cyan-400 hover:text-cyan-300 font-semibold transition-colors cursor-pointer">
                    ${isExpanded ? 'Show Less' : `<i class="fa-solid fa-chevron-down mr-1"></i> VIEW MORE COMMENTS (${remaining} more)`}
                </button>
            </div>
        `;
    }

    container.innerHTML = html;
}

function toggleExpandComments(postId) {
    if (postInteractions[postId]) {
        postInteractions[postId].isExpanded = !postInteractions[postId].isExpanded;
        renderCommentsUI(postId);
    }
}

async function handleDeleteComment(commentId, postId) {
    if (!token) return openAuthModal();

    try {
        const res = await fetch(`${API_BASE}/posts/comments/${commentId}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        });
        const data = await res.json();
        if (data.success) {
            await loadAndRenderComments(postId);
        }
    } catch (err) {
        console.error(err);
    }
}

async function handleAddComment(postId) {
    if (!token) return openAuthModal();
    const input = document.getElementById(`commentInput-${postId}`);
    if (!input || !input.value.trim()) return;

    try {
        const res = await fetch(`${API_BASE}/posts/${postId}/comments`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ content: input.value.trim() })
        });
        const data = await res.json();
        if (data.success) {
            input.value = "";
            await loadAndRenderComments(postId);
        }
    } catch (err) {
        console.error(err);
    }
}

function openAuthModal() { document.getElementById("authModal").classList.remove("hidden"); }
function closeAuthModal() { document.getElementById("authModal").classList.add("hidden"); }

function toggleAuthMode() {
    isRegisterMode = !isRegisterMode;
    document.getElementById("registerFields").classList.toggle("hidden", !isRegisterMode);
    document.getElementById("modalTitle").innerText = isRegisterMode ? "Create PlacementOS Account" : "Login to PlacementOS";
    document.getElementById("authSubmitBtn").innerText = isRegisterMode ? "Register" : "Login";
    document.getElementById("modalToggleText").innerText = isRegisterMode ? "Already have an account?" : "Don't have an account?";
    document.getElementById("modalToggleBtn").innerText = isRegisterMode ? "Login" : "Register";
}

async function handleAuthSubmit(e) {
    e.preventDefault();
    const email = document.getElementById("authEmail").value;
    const password = document.getElementById("authPassword").value;

    if (isRegisterMode) {
        const payload = {
            name: document.getElementById("authName").value,
            branch: document.getElementById("authBranch").value,
            batch: document.getElementById("authBatch").value,
            email, password, role: "STUDENT"
        };

        try {
            const regRes = await fetch(`${API_BASE}/auth/register`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            const regData = await regRes.json();

            if (!regData.success) {
                showCustomAlert(regData.message || "Registration failed!");
                return;
            }

            await performLogin(email, password);
        } catch (err) {
            showCustomAlert("Error connecting to server!");
        }
    } else {
        await performLogin(email, password);
    }
}

async function performLogin(email, password) {
    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();

        if (data.success) {
            const responseData = data.data;
            token = typeof responseData === 'string' ? responseData : (responseData.token || responseData.jwt);

            currentUser = responseData.user || {
                email: email,
                name: email.split("@")[0]
            };

            localStorage.setItem("token", token);
            localStorage.setItem("user", JSON.stringify(currentUser));

            closeAuthModal();
            updateAuthUI();
            reloadCurrentFeed();
        } else {
            showCustomAlert(data.message || "Login failed!");
        }
    } catch (err) {
        showCustomAlert("Error connecting to server!");
    }
}

function handleLogout() {
    localStorage.clear();
    token = null;
    currentUser = null;
    currentFeedMode = 'ALL';
    updateAuthUI();
    filterFeed('ALL');
}

async function handleCreatePost() {
    if (!token) return openAuthModal();

    const companyName = document.getElementById("postCompany").value;
    const title = document.getElementById("postTitle").value;
    const content = document.getElementById("postContent").value;

    if (!companyName || !title || !content) {
        return showCustomAlert("Please fill in all fields before submitting!");
    }

    try {
        const res = await fetch(`${API_BASE}/posts`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ companyName, title, content })
        });
        const data = await res.json();

        if (data.success) {
            document.getElementById("postCompany").value = "";
            document.getElementById("postTitle").value = "";
            document.getElementById("postContent").value = "";

            showSuccessModal("Post Successful! Your interview experience is now live on the feed.");
            reloadCurrentFeed();
        } else {
            showCustomAlert(data.message || "Post rejected automatically: Content must be placement or career related!");
        }
    } catch (err) {
        showCustomAlert("Failed to submit post due to server issue!");
    }
}